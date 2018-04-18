function calc_datum_minute_time_slots(node, sources, start_ts, span, slotsecs, tolerance) {
//	IN node bigint, 									108
//	IN sources text[], 									['Main']
//	IN start_ts timestamp with time zone, 				Date(...)
//	IN span interval, 									'01:00:00' for 1 hour
//	IN slotsecs integer DEFAULT 600,					600
//	IN tolerance interval DEFAULT interval '20 minutes'	'00:20:00' for 20 minutes
	
'use strict';
var runningAvgDiff,
	runningAvgMax = 5,
	toleranceMs = intervalMs(tolerance),
	hourFill = {'watts' : 'wattHours'},
	slotMode = (slotsecs > 0 && slotsecs < 3600),
	ignoreLogMessages = (slotMode === true || intervalMs(span) !== 3600000),
	logInsertStmt;

function intervalMs(intervalValue) {
	// calculate the number of milliseconds in the tolerance interval, which plv8 gives us as a string which we look for HH:MM:SS format
	var hms = intervalValue.match(/(\d{2}):(\d{2}):(\d{2})$/);
	if ( hms && hms.length === 4 ) {
		return ((hms[1] * 60 * 60 * 1000) + (hms[2] * 60 * 1000) + (hms[3] * 1000));
	}
	return null;
}

function logMessage(nodeId, sourceId, ts, msg) {
	if ( ignoreLogMessages ) {
		return;
	}
	var msg;
	if ( !logInsertStmt ) {
		logInsertStmt = plv8.prepare('INSERT INTO solaragg.agg_messages (node_id, source_id, ts, msg) VALUES ($1, $2, $3, $4)', 
			['bigint', 'text', 'timestamp with time zone', 'text']);
	}
	var dbMsg = Array.prototype.slice.call(arguments, 3).join(' ');
	logInsertStmt.execute([nodeId, sourceId, ts, dbMsg]);
}

function calculateAccumulatingValue(rec, r, val, prevVal, prop, ms) {
	var avgObj = r.accAvg[prop],
		offsetT = 0,
		diff,
		diffT,
		minutes;
	if ( 
			// disallow negative values for records tagged 'power', e.g. inverters that reset each night their reported accumulated energy
			(val < prevVal * 0.5 && rec.jdata.t && Array.isArray(rec.jdata.t) && rec.jdata.t.indexOf('power') >= 0)
			||
			// the running average is 0, the previous value > 0, and the current val <= 1.5% of previous value (i.e. close to 0);
			// don't treat this as a negative accumulation in this case if diff non trivial;
			(prevVal > 0 && (!avgObj || avgObj.average < 1) && val < (prevVal * 0.015))
			) {
		logMessage(node, r.source_id, new Date(rec.tsms), 'Forcing node prevVal', prevVal, 'to 0, val =', val);
		prevVal = 0;
	}
	diff = (val - prevVal);
	minutes = ms / 60000;
	diffT = Math.abs(diff / minutes);
	if ( avgObj ) {
		if ( avgObj.average > 0 ) {
			offsetT = (diffT / avgObj.average) 
				* (avgObj.next < runningAvgMax ? Math.pow(avgObj.next / runningAvgMax, 2) : 1)
				* (minutes > 2 ? 4 : Math.pow(minutes, 2));
		} else {
			offsetT = (diffT * (minutes > 5 ? 25 : Math.pow(minutes, 2)));
		}
	}
	if ( offsetT > 100 ) {
		logMessage(node, r.source_id, new Date(rec.tsms), 'Rejecting diff', diff, 'offset(t)', offsetT.toFixed(1), 
			'diff(t)', sn.math.util.fixPrecision(diffT, 100), '; ravg', (avgObj ? sn.math.util.fixPrecision(avgObj.average, 100) : 'N/A'), 
			(avgObj ? JSON.stringify(avgObj.samples.map(function(e) { return sn.math.util.fixPrecision(e, 100); })) : 'N/A'));
		return 0;
	}
	maintainAccumulatingRunningAverageDifference(r.accAvg, prop, diffT)
	return diff;
}

function maintainAccumulatingRunningAverageDifference(accAvg, prop, diff) {
	var i,
		avg = 0,
		avgObj = accAvg[prop],
		val,
		samples;
	if ( avgObj === undefined ) {
		avgObj = { samples : new Array(runningAvgMax), average : diff, next : 1 }; // wanted Float32Array, but not available in plv8
		avgObj.samples[0] = diff;
		for ( i = 1; i < runningAvgMax; i += 1 ) {
			avgObj.samples[i] = 0x7FC00000;
		}
		accAvg[prop] = avgObj;
		avg = diff;
	} else {
		samples = avgObj.samples;
		samples[avgObj.next % runningAvgMax] = diff;
		avgObj.next += 1;
		for ( i = 0; i < runningAvgMax; i += 1 ) {
			val = samples[i];
			if ( val === 0x7FC00000 ) {
				break;
			}
			avg += val;
		}
		avg /= i;
		avgObj.average = avg;
	}
}

function finishResultObject(r) {
	var prop,
		robj,
		ri,
		ra;
	if ( r.tsms < start_ts.getTime() ) {
		// not included in output because before time start
		return;
	}
	robj = {
		ts_start : new Date(r.tsms),
		source_id : r.source_id,
		jdata : {}
	};
	ri = sn.math.util.calculateAverages(r.iobj, r.iobjCounts);
	ra = r.aobj;
	
	for ( prop in ri ) {
		robj.jdata.i = ri;
		break;
	}
	for ( prop in ra ) {
		robj.jdata.a = sn.util.merge({}, ra); // call merge() to pick up sn.math.util.fixPrecision
		break;
	}

	if ( r.prevRec && r.prevRec.percent > 0 ) {
		// merge last record s obj into results, but not overwriting any existing properties
		if ( r.prevRec.jdata.s ) {
			for ( prop in r.prevRec.jdata.s ) {
				robj.jdata.s = r.prevRec.jdata.s;
				break;
			}
		}
		if ( Array.isArray(r.prevRec.jdata.t) && r.prevRec.jdata.t.length > 0 ) {
			robj.jdata.t = r.prevRec.jdata.t;
		}
	}
	plv8.return_next(robj);
}

function handleAccumulatingResult(rec, result) {
	var acc = rec.jdata.a,
		prevAcc = result.prevAcc,
		aobj = result.aobj,
		prop;
	if ( acc && prevAcc && rec.tdiffms <= toleranceMs ) {
		// accumulating data
		for ( prop in acc ) {
			if ( prevAcc[prop] !== undefined ) {				
				sn.math.util.addto(prop, calculateAccumulatingValue(rec, result, acc[prop], prevAcc[prop], prop, rec.tdiffms), aobj, rec.percent);
			}
		}
	}
}

function handleInstantaneousResult(rec, result, onlyHourFill) {
	var inst = rec.jdata.i,
		prevInst = result.prevInst,
		iobj = result.iobj,
		iobjCounts = result.iobjCounts,
		prop,
		propHour;
	if ( inst && rec.percent > 0 && rec.tdiffms <= toleranceMs ) {
		// instant data
		for ( prop in inst ) {
			if ( onlyHourFill !== true ) {
				// only add instantaneous average values for 100% records; we may have to use percent to hour-fill below
				sn.math.util.addto(prop, inst[prop], iobj, 1, iobjCounts);
			}
			if ( result.prevRec && hourFill[prop] ) {
				// calculate hour value, if not already defined for given property
				propHour = hourFill[prop];
				if ( !(rec.jdata.a && rec.jdata.a[propHour]) && prevInst && prevInst[prop] !== undefined ) {
					sn.math.util.addto(propHour, sn.math.util.calculateAverageOverHours(inst[prop], prevInst[prop], rec.tdiffms), result.aobj, rec.percent);
				}
			}
		}
	}
}

function handleFractionalAccumulatingResult(rec, result) {
	var fracRec = {
		source_id 	: rec.source_id,
		tsms		: result.prevRec.tsms,
		percent		: (1 - rec.percent),
		tdiffms		: rec.tdiffms,
		jdata		: rec.jdata
	};
	handleAccumulatingResult(fracRec, result);
	handleInstantaneousResult(fracRec, result, true);
}

(function() {
	var results = {}, // { ts_start : 123, source_id : 'A', aobj : {}, iobj : {}, iobjCounts : {}, sobj : {} ...}
		sourceId,
		result,
		rec,
		prop,
		stmt,
		cur,
		spanMs = intervalMs(span);
	
	if ( slotMode ) {
		stmt = plv8.prepare('SELECT source_id, tsms, percent, tdiffms, jdata FROM solaragg.find_datum_for_minute_time_slots($1, $2, $3, $4, $5, $6)', 
				['bigint', 'text[]', 'timestamp with time zone', 'interval', 'integer', 'interval']);
		cur = stmt.cursor([node, sources, start_ts, span, slotsecs, tolerance]);
	} else {
		stmt = plv8.prepare('SELECT source_id, tsms, percent, tdiffms, jdata FROM solaragg.find_datum_for_time_slot($1, $2, $3, $4, $5)', 
				['bigint', 'text[]', 'timestamp with time zone', 'interval', 'interval']);
		cur = stmt.cursor([node, sources, start_ts, span, tolerance]);
	}

	while ( rec = cur.fetch() ) {
		if ( !rec.jdata ) {
			continue;
		}
		sourceId = rec.source_id;
		result = results[sourceId];
		if ( result === undefined ) {
			result = { 
				tsms : (slotMode ? rec.tsms : start_ts.getTime()), 
				source_id : sourceId, 
				aobj : {}, 
				iobj : {}, 
				iobjCounts : {}, 
				sobj: {}, 
				accAvg : {}
			};
			results[sourceId] = result;
		} else if ( slotMode && rec.tsms !== result.tsms ) {
			if ( rec.percent < 1 && result.prevRec && result.prevRec.tsms >= start_ts.getTime() ) {
				// add 1-rec.percent to the previous time slot results
				handleFractionalAccumulatingResult(rec, result);
			}
			finishResultObject(result);
			result.tsms = rec.tsms;
			result.aobj = {};
			result.iobj = {};
			result.iobjCounts = {};
			result.sobj = {};
		}
	
		handleAccumulatingResult(rec, result);
		handleInstantaneousResult(rec, result);
	
		result.prevRec = rec;
		result.prevAcc = rec.jdata.a;
		result.prevInst = rec.jdata.i;
	}
	cur.close();
	stmt.free();

	for ( prop in results ) {
		finishResultObject(results[prop]);
	}
	
	if ( logInsertStmt ) {
		logInsertStmt.free();
	}
}());


} // function wrapper
