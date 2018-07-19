DELETE FROM public.plv8_modules WHERE module = 'datum/logEdgeAggregateMessage';
INSERT INTO public.plv8_modules (module, autoload, source) VALUES ('datum/logEdgeAggregateMessage', FALSE,
$FUNCTION$'use strict';

Object.defineProperty(exports, "__esModule", {
	value: true
});
exports.default = logEdgeAggregateMessage;
var logInsertStmt;

/**
 * Insert a log message into the <code>solaragg.agg_messages</code> table.
 *
 * @param {Number} EdgeId   The Edge ID to associated with the record.
 * @param {String} sourceId The source ID to associated with the record.
 * @param {Date}   ts       The timestamp to associate with the message.
 * @param {String} msg      The message to log. Any number of arguments may be included
 *                          after this argument, and they will be joined into a single
 *                          message, joined by a single space character.
 */
function logEdgeAggregateMessage(EdgeId, sourceId, ts, msg) {
	if (ignoreLogMessages || msg === undefined) {
		return;
	}
	if (!logInsertStmt) {
		logInsertStmt = plv8.prepare('INSERT INTO solaragg.agg_messages (Edge_id, source_id, ts, msg) VALUES ($1, $2, $3, $4)', ['bigint', 'text', 'timestamp with time zone', 'text']);
	}
	var dbMsg = Array.prototype.slice.call(arguments, 3).join(' ');
	logInsertStmt.execute([EdgeId, sourceId, ts, dbMsg]);
}$FUNCTION$);