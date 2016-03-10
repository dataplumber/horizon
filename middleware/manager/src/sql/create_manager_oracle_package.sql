--
-- Copyright (c) 2008, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: create_manager_oracle_package.sql 10118 2012-06-06 23:23:04Z nchung $
--

--
-- This SQL script creates the database functions and procedures for the Horizon domain schema.
--

create or replace
PACKAGE INGEST as

 PROCEDURE updateStorage(totalSize in NUMBER, lastMod in NUMBER, storageId in NUMBER);

 FUNCTION getStorage(storageType in VARCHAR2, totalSize in NUMBER, jobPriority in NUMBER) return Integer;
 
 FUNCTION getStorageByTypeAndPriority(storageType in VARCHAR2, jobPriority in NUMBER) return Integer;

END INGEST;
/
create or replace
PACKAGE BODY INGEST as

PROCEDURE updateStorage(totalSize in NUMBER, lastMod in NUMBER, storageId in NUMBER)
 is
  BEGIN

    update ING_LOCATION set last_used=lastMod, space_used=(space_used+totalSize) where id=storageId;

    return;
END updateStorage;

FUNCTION getStorage(storageType in VARCHAR2, totalSize in NUMBER, jobPriority in NUMBER)
return INTEGER AS

vEid ing_storage.id%TYPE;
  BEGIN

  select insid into vEid from (
    select * from 
      (select ins.id as insid, (loc.space_reserved - (loc.space_used + totalSize)) as sz, ins.priority 
        from ING_LOCATION loc, ING_STORAGE ins 
        where loc.id=ins.location_id and loc.stereotype = storageType and loc.active = 1 and (ins.priority = jobPriority or ins.priority is null)) x 
      left outer join 
      (select count(ing_engine_job.id) as job_count, contribute_storage_id 
        from ING_ENGINE_JOB 
        where priority = jobPriority 
        group by contribute_storage_id) y 
      on x.insid = y.contribute_storage_id 
      where sz > 0 
      order by job_count asc NULLS FIRST, sz desc, priority asc
  ) where rownum=1;

  return vEid;

end getStorage;

FUNCTION getStorageByTypeAndPriority(storageType in VARCHAR2, jobPriority in NUMBER)
return INTEGER AS

vEid ing_storage.id%TYPE;
  BEGIN

  select insid into vEid from (
    select ins.id as insid, priority 
      from ING_LOCATION loc, ING_STORAGE ins 
      left outer join 
      (select count(ing_engine_job.id) as job_count, contribute_storage_id 
        from ING_ENGINE_JOB 
        where priority = jobPriority 
        group by contribute_storage_id) y 
      on ins.id = y.contribute_storage_id where loc.id=ins.location_id and loc.stereotype = storageType and loc.active = 1 and (ins.priority = jobPriority or ins.priority is null)
      order by job_count asc NULLS FIRST, priority asc
  ) where rownum=1;

  return vEid;

end getStorageByTypeAndPriority;

END INGEST;
