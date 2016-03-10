/*
** Copyright (c) 2009, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
** 
** $Id: inv_granule_metrics_since.sql 2857 2009-04-30 02:07:51Z shardman $
*/

/*
** This script returns the ingested granule count and average amount
** of hours from ingest to archive per day for the last number of 
** days as specified in the first argument.
*/

SET HEADING OFF;

SELECT 'Report granule ingestion metrics.'
FROM dual;

SELECT 'Executed by ' || substr(user, 1, length(user)) ||
' at ' || to_char(sysdate,'YYYY-MM-DD"T"HH24:MI:SS".000"') || '.'
FROM dual;

SET HEADING ON;

COLUMN "Day-of-Year" FORMAT A11
COLUMN "Granules Ingested" FORMAT 999,999,999
COLUMN "Average Hours" FORMAT 999,990.999

SELECT TO_CHAR(ingest_time, 'YYYY-DDD') AS "Day-of-Year", COUNT(*) AS "Granule Count", AVG(TO_NUMBER(TO_CHAR(archive_time, 'J') - TO_CHAR(ingest_time, 'J')) * 86400 + TO_NUMBER(TO_CHAR(archive_time, 'SSSSS') - TO_CHAR(ingest_time, 'SSSSS'))) / 1440 AS "Average Hours"
FROM granule
WHERE status = 'ONLINE'
AND ingest_time > SYSDATE - &1
GROUP BY TO_CHAR(ingest_time, 'YYYY-DDD')
ORDER BY "Day-of-Year" DESC;
