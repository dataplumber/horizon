--
-- Copyright (c) 2013, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script creates the database functions and procedures for the DMAS Manager schema.
--

CREATE FUNCTION tie.updateStorage(
   NUMERIC,
   NUMERIC,
   NUMERIC) RETURNS integer AS '
DECLARE
   totalSize ALIAS FOR $1;
   lastMod ALIAS FOR $2;
   storageId ALIAS FOR $3;
BEGIN

    UPDATE ing_location
    SET last_used=lastMod,
        space_used=(space_used+totalSize)
    WHERE id=storageId;
    return 0;

END;
' LANGUAGE plpgsql;

CREATE FUNCTION tie.getStorage(
   VARCHAR,
   NUMERIC,
   NUMERIC) RETURNS NUMERIC(19, 0) AS '
DECLARE
   storageType ALIAS FOR $1;
   totalSize ALIAS FOR $2;
   jobPriority ALIAS FOR $3;
   vEid tie.ing_storage.id%TYPE;
BEGIN

  SELECT insid INTO vEid
  FROM (
     SELECT *
     FROM (

        SELECT ins.id AS insid,
               (loc.space_reserved - (loc.space_used + totalSize)) AS sz,
               ins.priority 
        FROM tie.ing_location loc, tie.ing_storage ins 
        WHERE loc.id=ins.location_id
           AND loc.stereotype = storageType
           AND loc.active = true
           AND (ins.priority = jobPriority OR ins.priority IS NULL)) AS x 

        LEFT OUTER JOIN (
           SELECT COUNT(ing_engine_job.id) AS job_count,
                  contribute_storage_id 
           FROM tie.ing_engine_job
           WHERE priority = jobPriority 
           GROUP BY contribute_storage_id) y 

        ON x.insid = y.contribute_storage_id 

      WHERE sz > 0 
      ORDER BY job_count ASC NULLS FIRST, sz DESC, priority ASC 
   ) AS y
   LIMIT 1;

  RETURN vEid;

END;
' LANGUAGE plpgsql;

CREATE FUNCTION tie.getStorageByTypeAndPriority(
   VARCHAR,
   NUMERIC) RETURNS NUMERIC(19, 0) AS '
DECLARE
   storageType ALIAS FOR $1;
   jobPriority ALIAS FOR $2;
   vEid tie.ing_storage.id%TYPE;
BEGIN

   SELECT insid INTO vEid 
   FROM (
      SELECT ins.id AS insid, priority 
      FROM tie.ing_location loc, tie.ing_storage ins 

      LEFT OUTER JOIN (
         SELECT COUNT(ing_engine_job.id) AS job_count, contribute_storage_id 
         FROM ING_ENGINE_JOB 
         WHERE priority = jobPriority 
         GROUP BY contribute_storage_id) AS y 

      ON ins.id = y.contribute_storage_id
      WHERE loc.id=ins.location_id
         AND loc.stereotype = storageType
         AND loc.active = true
         AND (ins.priority = jobPriority OR ins.priority IS NULL)
      ORDER BY job_count ASC NULLS FIRST, priority ASC
   ) AS x
   LIMIT 1;

   RETURN vEid;

END;
' LANGUAGE plpgsql;

