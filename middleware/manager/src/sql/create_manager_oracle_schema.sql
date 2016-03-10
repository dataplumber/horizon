--
-- Copyright (c) 2008, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: create_ingest_oracle_schema.sql 2698 2009-03-06 01:46:15Z axt $
--

--
-- This SQL script creates the database objects for the Horizon domain schema.
--


CREATE TABLE tie.ing_access_role(
   id           NUMBER(19, 0)     NOT NULL,   
   version      NUMBER(19, 0)     NOT NULL, 
   capabilities NUMBER(10, 0)     NOT NULL,    
   name         VARCHAR2(30 CHAR) NOT NULL,    
   
   CONSTRAINT ing_access_role_pk PRIMARY KEY(id),
   CONSTRAINT ing_access_role_uq UNIQUE(name)
);

CREATE TABLE tie.ing_data_file(
   id               NUMBER(19, 0) NOT NULL     ,
   version          NUMBER(19, 0) NOT NULL     , 
   checksum         VARCHAR2(40 CHAR)          ,
   compression      VARCHAR2(5 CHAR)           ,
   current_lock     VARCHAR2(8 CHAR)           ,
   file_size        NUMBER(19, 0)              ,
   ingest_completed NUMBER                     ,
   ingest_started   NUMBER                     ,  
   name             VARCHAR2(125 CHAR) NOT NULL,
   note             VARCHAR2(1024 CHAR)        ,
   product_id       NUMBER(19, 0) NOT NULL     ,
   provider_id      NUMBER(19, 0) NOT NULL     ,
   remote_path      VARCHAR2(255 CHAR) NOT NULL,
   
   CONSTRAINT ing_data_file_pk
      PRIMARY KEY(id),
      
   CONSTRAINT ing_data_file_uq 
      UNIQUE(product_id, name, current_lock)
);

CREATE TABLE tie.ing_engine_job(
   id             NUMBER(19, 0)     NOT NULL,
   version        NUMBER(19, 0)     NOT NULL, 
   assigned       NUMBER            NOT NULL,
   operation      VARCHAR2(9 CHAR)  NOT NULL,
   previous_state VARCHAR2(23 CHAR) NOT NULL,
   product_id     NUMBER(19, 0)     NOT NULL,
   path           VARCHAR2(255 CHAR)        ,
   priority       NUMBER(10, 0)     NOT NULL CHECK(priority BETWEEN 1 AND 3),
   contribute_storage_id NUMBER(19, 0) NOT NULL,
   
   CONSTRAINT ing_engine_job_pk PRIMARY KEY(id),
   CONSTRAINT ing_engine_job_uq UNIQUE(product_id, operation)  
);

CREATE TABLE tie.ing_federation(
   id      NUMBER(19, 0)      NOT NULL,
   version NUMBER(19, 0)      NOT NULL,
   name    VARCHAR2(30 CHAR)  NOT NULL,
   note    VARCHAR2(255 CHAR) NOT NULL,
   updated NUMBER                     ,
   hostname VARCHAR2(30 CHAR) NOT NULL,
   port     NUMBER(10, 0)     NOT NULL,
   
   CONSTRAINT ing_federation_pk PRIMARY KEY(id),
   CONSTRAINT ing_federation_uq UNIQUE(name)
);

CREATE TABLE tie.ing_message_schema(
   id       NUMBER(19, 0)      NOT NULL,
   version  NUMBER(19, 0)      NOT NULL, 
   released NUMBER             NOT NULL,
   revision VARCHAR2(255 CHAR) NOT NULL,
   SCHEMA   CLOB               NOT NULL,
   
   CONSTRAINT ing_message_schema_pk PRIMARY KEY(id),
   CONSTRAINT ing_message_schema_uq UNIQUE(revision)
);

CREATE TABLE tie.ing_product(
   id                   NUMBER(19, 0)     NOT NULL  ,
   version              NUMBER(19, 0)     NOT NULL  , 
   archive_note         VARCHAR2(3000 CHAR)         ,
   archive_text         CLOB                        ,
   archived_at          NUMBER                      ,
   complete_text        CLOB                        ,
   completed            NUMBER                      ,
   contribute_storage_id NUMBER(19, 0)               ,
   contributor          VARCHAR2(255 CHAR)  NOT NULL,
   created              NUMBER                      ,
   current_lock         VARCHAR2(16 CHAR)           ,
   current_retries      NUMBER(10, 0)  CHECK(current_retries BETWEEN 0 AND 10)     NOT NULL,
   current_state        VARCHAR2(23 CHAR)   NOT NULL,
   initial_text         CLOB                        ,
   inventory_id         NUMBER(19, 0)               ,
   local_relative_path  VARCHAR2(255 CHAR)          ,
   name                 VARCHAR2(125 CHAR)  NOT NULL,
   note                 VARCHAR2(3000 CHAR)         ,
   notify               NUMBER(1, 0)        NOT NULL,
   product_type_id      NUMBER(19, 0)       NOT NULL,
   readers              NUMBER(10, 0)               ,
   remote_relative_path VARCHAR2(255 CHAR)          ,
   schema_id            NUMBER(19, 0)               ,
   updated              NUMBER                      ,
   version_number       NUMBER(10, 0)       NOT NULL,
   version_string       VARCHAR2(255 CHAR)          ,

   CONSTRAINT ing_product_pk PRIMARY KEY(id),
   CONSTRAINT ing_product_uq UNIQUE(product_type_id, name, version_string)
);

CREATE TABLE tie.ing_event_category (
   id                   NUMBER(19, 0)     NOT NULL  ,
   version              NUMBER(19, 0)     NOT NULL  , 
   name                 VARCHAR2(100 CHAR) NOT NULL  ,

   CONSTRAINT ing_event_category_uq UNIQUE(name)
);

CREATE TABLE tie.ing_product_type(
   id                 NUMBER(19, 0)      NOT NULL,
   version            NUMBER(19, 0)      NOT NULL,
   federation_id      NUMBER(19, 0)      NOT NULL,
   ingest_only        NUMBER(1, 0)       NOT NULL,
   locked             NUMBER(1, 0)       NOT NULL,
   locked_at          NUMBER                     ,
   locked_by          VARCHAR2(255 CHAR)         ,
   name               VARCHAR2(100 CHAR)  NOT NULL,
   note               VARCHAR2(255 CHAR)         ,
   purge_rate         NUMBER(10, 0)      NOT NULL,
   relative_path      VARCHAR2(255 CHAR) NOT NULL,
   updated            NUMBER             NOT NULL,
   updated_by_id      NUMBER(19, 0)      NOT NULL,
   event_category_id  NUMBER(19, 0)              ,
   delivery_rate      NUMBER(10, 0)              ,
   priority           NUMBER(10, 0)      NOT NULL CHECK(priority BETWEEN 1 AND 3),
   
   
   CONSTRAINT ing_product_type_pk PRIMARY KEY(id),
   CONSTRAINT ing_product_type_uq UNIQUE(federation_id, name)
);

CREATE TABLE tie.ing_product_type_role(
   id              NUMBER(19, 0) NOT NULL,
   version         NUMBER(19, 0) NOT NULL,
   product_type_id NUMBER(19, 0) NOT NULL,
   role_id         NUMBER(19, 0) NOT NULL,
   
   CONSTRAINT ing_product_type_role_pk PRIMARY KEY(id),
   CONSTRAINT ing_proudct_type_role_uq UNIQUE(product_type_id, role_id)
);

CREATE TABLE tie.ing_remote_system(
   id              NUMBER(19, 0)      NOT NULL,
   version         NUMBER(19, 0)      NOT NULL, 
   created         NUMBER                     ,
   max_connections NUMBER(10, 0)      NOT NULL,
   note            VARCHAR2(255 CHAR)         ,
   organization    VARCHAR2(255 CHAR) NOT NULL,
   password        VARCHAR2(255 CHAR)         ,
   root_uri        VARCHAR2(255 CHAR) NOT NULL,
   updated         NUMBER                     ,
   updated_by      VARCHAR2(255 CHAR)         ,
   username        VARCHAR2(255 CHAR)         ,
   
   CONSTRAINT ing_remote_system_pk PRIMARY KEY(id),
   CONSTRAINT ing_remote_system_uq UNIQUE(root_uri)
);

CREATE TABLE tie.ing_storage(
   id                     NUMBER(19, 0)      NOT NULL,
   version                NUMBER(19, 0)      NOT NULL,
   created                NUMBER                     ,
   name                   VARCHAR2(255 CHAR) NOT NULL,
   priority               NUMBER(10, 0)      CHECK(priority BETWEEN 1 AND 3),
   location_id            NUMBER(19, 0)      NOT NULL,
   
   CONSTRAINT ing_storage_pk PRIMARY KEY(id),
   CONSTRAINT ing_storage_uq UNIQUE(name)  
);

CREATE TABLE tie.ing_location(
   id                     NUMBER(19, 0)      NOT NULL,
   version                NUMBER(19, 0)      NOT NULL,
   created                NUMBER                     ,
   last_used              NUMBER                     ,
   local_path             VARCHAR2(255 CHAR) NOT NULL,
   remote_access_protocol VARCHAR2(5 CHAR)           ,
   remote_path            VARCHAR2(255 CHAR) NOT NULL,
   space_reserved         NUMBER(19, 0)      NOT NULL,
   space_threshold        NUMBER(19, 0)              ,
   space_used             NUMBER(19, 0)              ,
   stereotype             VARCHAR2(30 CHAR)  NOT NULL,
   hostname               VARCHAR2(30 CHAR)  NOT NULL,
   active                 NUMBER(1, 0)       NOT NULL,
   
   CONSTRAINT ing_location_pk PRIMARY KEY(id),
   CONSTRAINT ing_location_uq UNIQUE(local_path)  
);

CREATE TABLE tie.ing_system_user(
   id        NUMBER(19, 0)      NOT NULL,
   version   NUMBER(19, 0)      NOT NULL,
   admin     NUMBER(1, 0)       NOT NULL,
   email     VARCHAR2(255 CHAR) NOT NULL,
   fullname  VARCHAR2(40 CHAR)  NOT NULL,
   name      VARCHAR2(180 CHAR) NOT NULL,
   note      VARCHAR2(255 CHAR)         ,
   password  VARCHAR2(40 CHAR)  NOT NULL,
   read_all  NUMBER(1, 0)       NOT NULL,
   write_all NUMBER(1, 0)       NOT NULL,

   CONSTRAINT ing_system_user_pk PRIMARY KEY(id),
   CONSTRAINT ing_system_user_uq UNIQUE(name)
);

CREATE TABLE tie.ing_system_user_role(
   id      NUMBER(19, 0) NOT NULL,
   version NUMBER(19, 0) NOT NULL,
   role_id NUMBER(19, 0) NOT NULL,
   user_id NUMBER(19, 0) NOT NULL,

   CONSTRAINT ing_system_user_role_pk PRIMARY KEY(id),
   CONSTRAINT ing_system_user_role_uq UNIQUE(user_id, role_id) 
);

CREATE TABLE tie.ing_system_user_session(
   id              NUMBER(19, 0)      NOT NULL,
   version         NUMBER(19, 0)      NOT NULL,
   expire_time     NUMBER                     ,
   issue_time      NUMBER             NOT NULL,
   product_type_id NUMBER(19, 0)              ,
   token           VARCHAR2(255 CHAR) NOT NULL,
   user_id         NUMBER(19, 0)      NOT NULL,
   
   CONSTRAINT ing_system_user_session_pk PRIMARY KEY(id),
   CONSTRAINT ing_system_user_session_uq UNIQUE(user_id, product_type_id) 
);

ALTER TABLE tie.ing_data_file ADD 
   CONSTRAINT ing_data_file_fk1
   FOREIGN KEY(provider_id) 
   REFERENCES tie.ing_remote_system(id);
   
ALTER TABLE tie.ing_data_file ADD 
   CONSTRAINT ing_data_file_fk2
   FOREIGN KEY(product_id) 
   REFERENCES tie.ing_product(id);

ALTER TABLE tie.ing_engine_job ADD 
   CONSTRAINT ing_engine_job_fk2 
   FOREIGN KEY(product_id) 
   REFERENCES tie.ing_product(id);

ALTER TABLE tie.ing_engine_job ADD 
   CONSTRAINT ing_engine_job_fk3
   FOREIGN KEY(contribute_storage_id) 
   REFERENCES tie.ing_storage(id);

ALTER TABLE tie.ing_product ADD 
   CONSTRAINT ing_product_fk1 
   FOREIGN KEY(product_type_id) 
   REFERENCES tie.ing_product_type(id);

ALTER TABLE tie.ing_product ADD 
   CONSTRAINT ing_product_fk2 
   FOREIGN KEY(schema_id) 
   REFERENCES tie.ing_message_schema(id);

ALTER TABLE tie.ing_product ADD 
   CONSTRAINT ing_product_fk3
   FOREIGN KEY(contribute_storage_id) 
   REFERENCES tie.ing_storage(id);

ALTER TABLE tie.ing_product_type ADD 
   CONSTRAINT ing_product_type_fk1 
   FOREIGN KEY(updated_by_id) 
   REFERENCES tie.ing_system_user(id);

ALTER TABLE tie.ing_product_type ADD 
   CONSTRAINT ing_product_type_fk2 
   FOREIGN KEY(federation_id) 
   REFERENCES tie.ing_federation(id);

ALTER TABLE tie.ing_product_type_role ADD 
   CONSTRAINT ing_product_type_role_fk1 
   FOREIGN KEY(role_id) 
   REFERENCES tie.ing_access_role(id);

ALTER TABLE tie.ing_product_type_role ADD 
   CONSTRAINT ing_product_type_role_fk2 
   FOREIGN KEY(product_type_id) 
   REFERENCES tie.ing_product_type(id);

ALTER TABLE tie.ing_system_user_role ADD 
   CONSTRAINT ing_system_user_role_fk1 
   FOREIGN KEY(user_id) 
   REFERENCES tie.ing_system_user(id);

ALTER TABLE tie.ing_system_user_role ADD 
   CONSTRAINT ing_system_user_role_fk2 
   FOREIGN KEY(role_id) 
   REFERENCES tie.ing_access_role(id);

ALTER TABLE tie.ing_system_user_session ADD 
   CONSTRAINT ing_system_user_session_fk1 
   FOREIGN KEY(user_id) 
   REFERENCES tie.ing_system_user(id);

ALTER TABLE tie.ing_system_user_session ADD 
   CONSTRAINT ing_system_user_session_fk2
   FOREIGN KEY(product_type_id) 
   REFERENCES tie.ing_product_type(id);

ALTER TABLE tie.ing_storage ADD 
   CONSTRAINT ing_storage_fk1 
   FOREIGN KEY(location_id) 
   REFERENCES tie.ing_location(id);

CREATE SEQUENCE tie.hibernate_sequence
   NOCACHE;
CREATE SEQUENCE tie.ing_federation_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_systemuser_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_systemusersession_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_accessrole_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_systemuserrole_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_producttype_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_producttyperole_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_product_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_product_version_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_data_file_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_storage_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_enginejob_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_remotesystem_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_messageschema_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_event_category_id_seq
   NOCACHE;
CREATE SEQUENCE tie.ing_location_id_seq
   NOCACHE;
   
