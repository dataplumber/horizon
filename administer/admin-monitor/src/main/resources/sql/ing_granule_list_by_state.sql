/*
** Copyright (c) 2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
** 
** $Id: ing_granule_list_by_state.sql 2404 2008-12-17 01:31:18Z shardman $
*/

/*
** This script returns a list of granules for the specified state.
*/

SET HEADING OFF;

SELECT 'Monitor granules by state.'
FROM dual;

SELECT 'Executed by ' || substr(user, 1, length(user)) ||
' at ' || to_char(sysdate,'YYYY-MM-DD"T"HH24:MI:SS".000"') || '.'
FROM dual;

SET HEADING ON;

COLUMN "Granule Name" FORMAT A60

SELECT p.name AS "Granule Name", TO_CHAR(p.updated, 'YYYY-DDD"T"HH24:MI:SS.FF3') AS "Last Updated"
FROM ing_product p
WHERE p.current_state LIKE '&1'
ORDER BY p.updated DESC;

SET HEADING OFF

SELECT 'The following notes correspond with the above granules.'
FROM dual;

SELECT p.note AS "Ingest Notes"
FROM ing_product p
WHERE p.current_state LIKE '&1'
ORDER BY p.updated DESC;

