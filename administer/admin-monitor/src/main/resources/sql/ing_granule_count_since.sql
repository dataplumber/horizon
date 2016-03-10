/*
** Copyright (c) 2008-2009, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
** 
** $Id: ing_granule_count_since.sql 2595 2009-02-10 06:13:51Z shardman $
*/

/*
** This script returns the number of granules ingested by product type
** for the last number of hours as specified in the first argument.
*/

SET HEADING OFF;

SELECT 'Monitor recent granule ingestion activity.'
FROM dual;

SELECT 'Executed by ' || substr(user, 1, length(user)) ||
' at ' || to_char(sysdate,'YYYY-MM-DD"T"HH24:MI:SS".000"') || '.'
FROM dual;

SET HEADING ON;

COLUMN "Product Type" FORMAT A42
COLUMN "Granule Count" FORMAT 999,999,999

SELECT p.current_state as "Current State", COUNT(*) AS "Granule Count"
FROM ing_product p
WHERE p.created > SYSDATE - &1/24
GROUP BY p.current_state;

SELECT pt.name AS "Product Type", (SELECT COUNT(*) FROM ing_product p WHERE pt.id = p.product_type_id AND p.created > SYSDATE - &1/24) AS "Granule Count", (SELECT TO_CHAR(MAX(created), 'YYYY-DDD"T"HH24:MI:SS.FF3') FROM ing_product p WHERE pt.id = p.product_type_id AND p.created > SYSDATE - &1/24) AS "Last Granule Received"
FROM ing_product_type pt
WHERE (SELECT COUNT(*) FROM ing_product p WHERE pt.id = p.product_type_id AND p.created > SYSDATE - &1/24) > 0
ORDER BY "Granule Count" DESC, "Product Type" ASC;
