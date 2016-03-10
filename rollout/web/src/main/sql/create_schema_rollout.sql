/*
** Copyright (c) 2007-2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: create_schema.sql 3029 2009-06-12 21:48:51Z gangl $
*/

/*
** This SQL script creates the database objects for the Inventory schema.
*/

/*
** Create Tables
*/

/*
** collection
*/
CREATE TABLE rollout_collection
(
  rollout_dataset_id	NUMBER		,
  collection_id         NUMBER          ,
  short_name            VARCHAR2(80)    ,
  long_name             VARCHAR2(1024)          ,
  type                  VARCHAR2(20)    ,
  description           VARCHAR2(4000)          ,
  visible_flag          CHAR(1)         
);

 
/*
** contact
*/
CREATE TABLE rollout_contact
(
  rollout_dataset_id	NUMBER		,
  contact_id            NUMBER          ,
  role                  VARCHAR2(40)    ,
  first_name            VARCHAR2(80)    ,
  middle_name           VARCHAR2(80)            ,
  last_name             VARCHAR2(80)    ,
  email                 VARCHAR2(255)   ,
  phone                 VARCHAR2(80)            ,
  fax                   VARCHAR2(80)            ,
  address               VARCHAR2(512)           ,
  provider_id           NUMBER                  ,
  notify_type           VARCHAR2(20)            
);

/*
** dataset
*/
CREATE TABLE rollout_dataset
(
  rollout_dataset_id    NUMBER          ,
  provider_id           NUMBER          ,
  short_name            VARCHAR2(160)   ,
  long_name             VARCHAR2(255)   ,
  original_provider     VARCHAR2(160)           ,
  provider_dataset_name VARCHAR2(160)           ,
  processing_level      VARCHAR2(10)            ,
  region                VARCHAR2(80)    ,
  region_detail         VARCHAR2(80)            ,
  latitude_resolution   VARCHAR2(80)    ,
  longitude_resolution  VARCHAR2(80)    ,
  horizontal_resolution_range  VARCHAR2(80)     ,
  altitude_resolution   VARCHAR2(80)            ,
  depth_resolution      VARCHAR2(80)            ,
  temporal_resolution   VARCHAR2(1024)          ,
  temporal_resolution_range    VARCHAR2(80)     ,
  ellipsoid_type        VARCHAR2(160)           ,
  projection_type       VARCHAR2(160)           ,
  projection_detail     VARCHAR2(1024)          ,
  reference             VARCHAR2(1024)          ,
  description            VARCHAR2(4000)                    ,
  rollout_type		VARCHAR2(80)            ,
  CONSTRAINT rollout_dataset_pk_l PRIMARY KEY (rollout_dataset_id)
);

/*
** dataset_citation
*/
CREATE TABLE rollout_dataset_citation
(
  rollout_dataset_id            NUMBER          ,
  title                 VARCHAR2(255)   ,
  creator               VARCHAR2(255)   ,
  version               VARCHAR2(80)    ,
  publisher             VARCHAR2(100)           ,
  series_name           VARCHAR2(160)           ,
  release_date          VARCHAR2(160)                    ,
  release_place         VARCHAR2(100)           ,
  citation_detail       VARCHAR2(255)           ,
  online_resource       VARCHAR2(255)               
);


/*
** dataset_coverage
*/
CREATE TABLE rollout_dataset_coverage
(
  rollout_dataset_id            NUMBER          ,
  start_time_date            VARCHAR2(80)       ,
  start_time_time            VARCHAR2(80)       ,
  stop_time_date            VARCHAR2(80)       ,
  stop_time_time            VARCHAR2(80)       ,
  north_lat             NUMBER                  ,
  south_lat             NUMBER                  ,
  east_lon              NUMBER                  ,
  west_lon              NUMBER                  ,
  min_altitude          NUMBER                  ,
  max_altitude          NUMBER                  ,
  min_depth             NUMBER                  ,
  max_depth             NUMBER                  
);

/*
** dataset_location_policy
*/
CREATE TABLE rollout_location_policy
(
  rollout_dataset_id    NUMBER          ,
  type                  VARCHAR2(20)    ,
  base_path             VARCHAR2(255)  
);


/*
** dataset_parameter
*/
CREATE TABLE rollout_dataset_parameter
(
  rollout_dataset_id            NUMBER          ,
  category              VARCHAR2(80)    ,
  topic                 VARCHAR2(80)    ,
  term                  VARCHAR2(80)    ,
  variable              VARCHAR2(80)    ,
  variable_detail       VARCHAR2(160)            
);

/*
** dataset_policy
*/
CREATE TABLE rollout_dataset_policy
(
  rollout_dataset_id    NUMBER          ,
  data_class            VARCHAR2(20)    ,
  data_frequency        NUMBER                  ,
  data_volume           NUMBER                  ,
  data_duration         NUMBER                  ,
  access_type           VARCHAR2(20)    ,
  base_path_append_type VARCHAR2(20)    ,
  data_format           VARCHAR2(20)    ,
  compress_type         VARCHAR2(20)    ,
  checksum_type         VARCHAR2(20)    ,
  spatial_type          VARCHAR2(20)    ,
  access_constraint     VARCHAR2(1024)  ,
  use_constraint        VARCHAR2(1024)  
);

/*
** dataset_resource
*/
CREATE TABLE rollout_dataset_resource
(
  rollout_dataset_id    NUMBER          ,
  name                  VARCHAR2(80)            ,     
  path                  VARCHAR2(255)   ,
  type                  VARCHAR2(31)            ,
  description           VARCHAR2(1024)           
);

/*
** dataset_source
*/
CREATE TABLE rollout_dataset_sensor_source
(      
  rollout_dataset_id    NUMBER          ,
  source_id             NUMBER          ,
  sensor_id             NUMBER          ,
  sensor_short_name            VARCHAR2(31)    ,
  sensor_long_name             VARCHAR2(80)            ,
  sensor_swath_width           NUMBER                  ,
  sensor_description           VARCHAR2(1024)       ,
  source_short_name            VARCHAR2(31)    ,
  source_long_name             VARCHAR2(80)            ,
  source_type                  VARCHAR2(80)            ,
  source_orbit_period          NUMBER                  ,
  source_incl_angle            NUMBER                  ,
  source_description           VARCHAR2(1024)        
);

/*
** project
*/
CREATE TABLE rollout_project
(
  rollout_dataset_id	NUMBER		,
  project_id            NUMBER          ,
  short_name            VARCHAR2(31)    ,
  long_name             VARCHAR2(80)   
);

/*
** provider
*/
CREATE TABLE rollout_provider
(
  rollout_dataset_id	NUMBER		,
  provider_id           NUMBER          ,
  short_name            VARCHAR2(31)    ,
  long_name             VARCHAR2(160)   ,
  type                  VARCHAR2(20)    
);

/*
** provider_resource
*/
CREATE TABLE rollout_provider_resource
(
  rollout_dataset_id	NUMBER	,
  path                  VARCHAR2(255)   
);
  
/*
** Create Sequences
*/

CREATE SEQUENCE rollout_dataset_id_seq
  NOCACHE;


