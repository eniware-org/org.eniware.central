/**
 * Find source IDs matching a datum metadata search filter.
 *
 * Search filters are specified using LDAP filter syntax, e.g. <code>(/m/foo=bar)</code>.
 *
 * @param Edges				array of Edge IDs
 * @param criteria			the search filter
 *
 * @returns All matching source IDs.
 */
CREATE OR REPLACE FUNCTION eniwaredatum.find_sources_for_meta(
    IN Edges bigint[],
    IN criteria text
  )
  RETURNS TABLE(Edge_id eniwarecommon.Edge_id, source_id eniwarecommon.source_id)
  LANGUAGE plv8 ROWS 100 STABLE AS
$BODY$
'use strict';

var objectPathMatcher = require('util/objectPathMatcher').default,
	searchFilter = require('util/searchFilter').default;

var filter = searchFilter(criteria),
	stmt,
	curs,
	rec,
	meta,
	matcher,
	resultRec = {};

if ( !filter.rootEdge ) {
	plv8.elog(NOTICE, 'Malformed search filter:', criteria);
	return;
}

stmt = plv8.prepare('SELECT Edge_id, source_id, jdata FROM eniwaredatum.da_meta WHERE Edge_id = ANY($1)', ['bigint[]']);
curs = stmt.cursor([Edges]);

while ( rec = curs.fetch() ) {
	meta = rec.jdata;
	matcher = objectPathMatcher(meta);
	if ( matcher.matchesFilter(filter) ) {
		resultRec.Edge_id = rec.Edge_id;
		resultRec.source_id = rec.source_id;
		plv8.return_next(resultRec);
	}
}

curs.close();
stmt.free();

$BODY$;

/**
 * Find source IDs matching a location metadata search filter.
 *
 * Search filters are specified using LDAP filter syntax, e.g. <code>(/m/foo=bar)</code>.
 *
 * @param locs				array of location IDs
 * @param criteria			the search filter
 *
 * @returns All matching source IDs.
 */
CREATE OR REPLACE FUNCTION eniwaredatum.find_sources_for_loc_meta(
    IN locs bigint[],
    IN criteria text
  )
  RETURNS TABLE(loc_id eniwarecommon.loc_id ,source_id eniwarecommon.source_id)
  LANGUAGE plv8 ROWS 100 STABLE AS
$BODY$
'use strict';

var objectPathMatcher = require('util/objectPathMatcher').default,
	searchFilter = require('util/searchFilter').default;

var filter = searchFilter(criteria),
	stmt,
	curs,
	rec,
	meta,
	matcher,
	resultRec = {};

if ( !filter.rootEdge ) {
	plv8.elog(NOTICE, 'Malformed search filter:', criteria);
	return;
}

stmt = plv8.prepare('SELECT loc_id, source_id, jdata FROM eniwaredatum.da_loc_meta WHERE loc_id = ANY($1)', ['bigint[]']);
curs = stmt.cursor([locs]);

while ( rec = curs.fetch() ) {
	meta = rec.jdata;
	matcher = objectPathMatcher(meta);
	if ( matcher.matchesFilter(filter) ) {
		resultRec.loc_id = rec.loc_id;
		resultRec.source_id = rec.source_id;
		plv8.return_next(resultRec);
	}
}

curs.close();
stmt.free();

$BODY$;
