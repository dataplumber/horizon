/*
** Copyright (c) 2009, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
** 
** $Id: inv_granule_overdue.sql 2597 2009-02-10 06:38:36Z shardman $
*/

/*
** This script returns each dataset where granules have not been
** received within the policy's specified frequency.
*/

SET HEADING OFF;

SELECT 'Monitor overdue granule ingestion.'
FROM dual;

SELECT 'Executed by ' || substr(user, 1, length(user)) ||
' at ' || to_char(sysdate,'YYYY-MM-DD"T"HH24:MI:SS".000"') || '.'
FROM dual;

SET HEADING ON;

COLUMN "Dataset" FORMAT A36
COLUMN "Frequency in Hours" FORMAT 999,999,999
COLUMN "Hours Overdue" FORMAT 999,999,999

SELECT distinct d.short_name AS "Dataset", dp.data_frequency AS "Frequency in Hours", (SELECT ((TO_NUMBER(TO_CHAR(SYSDATE, 'J') - TO_CHAR(MAX(g.ingest_time), 'J')) * 86400) + TO_NUMBER(TO_CHAR(SYSDATE, 'SSSSS') - TO_CHAR(MAX(g.ingest_time), 'SSSSS'))) / 3600 FROM granule g WHERE g.dataset_id = d.dataset_id) AS "Hours Overdue", (SELECT TO_CHAR(MAX(g.ingest_time), 'YYYY-DDD"T"HH24:MI:SS.FF3') FROM granule g WHERE g.dataset_id = d.dataset_id) AS "Last Granule Ingested"
FROM dataset d, dataset_policy dp, dataset_coverage dc
WHERE d.dataset_id = dp.dataset_id
AND dp.data_frequency IS NOT NULL
AND d.dataset_id = dc.dataset_id
AND dc.stop_time IS NULL
AND (SELECT ((TO_NUMBER(TO_CHAR(SYSDATE, 'J') - TO_CHAR(MAX(g.ingest_time), 'J')) * 86400) + TO_NUMBER(TO_CHAR(SYSDATE, 'SSSSS') - TO_CHAR(MAX(g.ingest_time), 'SSSSS'))) / 3600 FROM granule g WHERE g.dataset_id = d.dataset_id) > dp.data_frequency
ORDER BY "Hours Overdue" DESC;
