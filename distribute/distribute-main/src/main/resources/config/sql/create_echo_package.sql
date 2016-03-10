/*
** Copyright (c) 2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id$
*/

/*
** This SQL script creates the ECHO package for the Distribute schema.
*/

CREATE OR REPLACE PACKAGE echo IS
TYPE ref_cursor IS REF CURSOR;

FUNCTION get_collection_list
RETURN ref_cursor;

FUNCTION get_collection (c_id NUMBER)
RETURN ref_cursor;

FUNCTION get_version (d_id NUMBER)
RETURN ref_cursor;

FUNCTION get_contact (c_id NUMBER)
RETURN ref_cursor;

FUNCTION get_parameter (d_id NUMBER)
RETURN ref_cursor;

FUNCTION get_platform (d_id NUMBER)
RETURN ref_cursor;

FUNCTION get_access (d_id NUMBER)
RETURN ref_cursor;

FUNCTION get_resource (d_id NUMBER)
RETURN ref_cursor;

END echo;
/


CREATE OR REPLACE PACKAGE BODY echo AS

/*
** Retrieve the collection list based on whether the collection
** has been updated and should be submitted to ECHO.
*/
FUNCTION get_collection_list
  RETURN ref_cursor IS
  cCursor echo.ref_cursor;

BEGIN
  OPEN cCursor FOR

  SELECT p.collection_id, p.echo_submit_date, cd.dataset_id
  FROM collection_product p, collection_dataset cd
  WHERE p.visible_flag = 'Y' AND p.collection_id = cd.collection_id;

  RETURN cCursor;

END get_collection_list;

/*
** Retrieve collection level information based on the collection identifier.
*/
FUNCTION get_collection (c_id IN NUMBER)
  RETURN ref_cursor IS
  cCursor echo.ref_cursor;
 
BEGIN
  OPEN cCursor FOR

  SELECT c.short_name, c.long_name, c.description, substr(d.original_provider, 1, 80), d.processing_level, dp.data_format, d.region, dc.start_time, dc.stop_time, d.dataset_id
  FROM collection c, collection_dataset cd, dataset d, dataset_policy dp, dataset_coverage dc
  WHERE c.collection_id = c_id AND c.collection_id = cd.collection_id AND cd.dataset_id = d.dataset_id AND d.dataset_id = dp.dataset_id AND d.dataset_id = dc.dataset_id;

  RETURN cCursor;

END get_collection;

/*
** Retrieve version information based on the dataset identifier. This
** is definitely not the best way to do this but it will get the job done.
*/
FUNCTION get_version (d_id IN NUMBER)
  RETURN ref_cursor IS
  vCursor echo.ref_cursor;
 
BEGIN
  OPEN vCursor FOR

  SELECT max(dv.version_id), min(dmh.creation_date), max(last_revision_date)
  FROM dataset_version dv, dataset_meta_history dmh
  WHERE dv.dataset_id = d_id AND dmh.dataset_id = d_id;
  
  RETURN vCursor;

END get_version;

/*
** Retrieve contact information based on the collection identifier.
*/
FUNCTION get_contact (c_id IN NUMBER)
  RETURN ref_cursor IS
  cCursor echo.ref_cursor;

BEGIN
  OPEN cCursor FOR

  SELECT p.long_name, c.phone, c.email, c.first_name, c.middle_name, c.last_name, c.role
  FROM collection_contact cc, contact c, provider p
  WHERE cc.collection_id = c_id AND cc.contact_id = c.contact_id AND c.provider_id = p.provider_id;

  RETURN cCursor; 

END get_contact;

/*
** Retrieve parameter information based on the dataset identifier.
*/
FUNCTION get_parameter (d_id IN NUMBER)
  RETURN ref_cursor IS
  sCursor echo.ref_cursor;

BEGIN
  OPEN sCursor FOR

  SELECT unique category, topic, term, variable  
  FROM dataset_parameter
  WHERE dataset_id = d_id;
 
  RETURN sCursor; 
  
END get_parameter;

/*
** Retrieve platform (source and sensor) information based on the
** dataset identifier.
*/
FUNCTION get_platform (d_id IN NUMBER)
  RETURN ref_cursor IS
  tCursor echo.ref_cursor;

BEGIN
  OPEN tCursor FOR

  SELECT s.short_name, s.long_name, s.type, n.short_name, n.long_name       
  FROM source s, sensor n, dataset_source ds
  WHERE s.source_id = n.source_id AND n.sensor_id = ds.sensor_id AND ds.source_id = s.source_id AND dataset_id = d_id;
 
  RETURN tCursor; 
  
END get_platform;

/*
** Retrieve online access information based on the dataset identifier.
*/
FUNCTION get_access (d_id IN NUMBER)
  RETURN ref_cursor IS
  aCursor echo.ref_cursor;

BEGIN
  OPEN aCursor FOR

  SELECT base_path, type
  FROM dataset_location_policy
  WHERE dataset_id = d_id AND (type like '%FTP' OR type like '%OPENDAP');
 
  RETURN aCursor; 
  
END get_access;

/*
** Retrieve online resource information based on the dataset identifier.
*/
FUNCTION get_resource (d_id IN NUMBER)
  RETURN ref_cursor IS
  rCursor echo.ref_cursor;

BEGIN
  OPEN rCursor FOR

  SELECT unique path, description, type
  FROM dataset_resource
  WHERE dataset_id = d_id;
 
  RETURN rCursor; 
  
END get_resource;

END echo;
/
