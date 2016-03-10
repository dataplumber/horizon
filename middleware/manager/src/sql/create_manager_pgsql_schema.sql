--
-- Copyright (c) 2013, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script creates the database objects for the DMAS Manager schema.
--


CREATE TABLE tie.ing_access_role(
   id            NUMERIC(19, 0)  NOT NULL,   
   version       NUMERIC(19, 0)  NOT NULL, 
   capabilities  NUMERIC(10, 0)  NOT NULL,    
   name          VARCHAR(30)     NOT NULL,    
   
   CONSTRAINT ing_access_role_pk PRIMARY KEY(id),
   CONSTRAINT ing_access_role_uq UNIQUE(name)
);

CREATE TABLE tie.ing_data_file(
   id                NUMERIC(19, 0)  NOT NULL,
   version           NUMERIC(19, 0)  NOT NULL, 
   checksum          VARCHAR(40)     NULL,
   compression       VARCHAR(5)      NULL,
   current_lock      VARCHAR(8)      NULL,
   file_size         NUMERIC(19, 0)  NULL,
   ingest_completed  BIGINT          NULL,
   ingest_started    BIGINT          NULL,  
   name              VARCHAR(125)    NOT NULL,
   note              VARCHAR(1024)   NULL,
   product_id        NUMERIC(19, 0)  NOT NULL,
   provider_id       NUMERIC(19, 0)  NULL,
   remote_path       VARCHAR(255)    NOT NULL,
   
   CONSTRAINT ing_data_file_pk
      PRIMARY KEY(id),
      
   CONSTRAINT ing_data_file_uq 
      UNIQUE(product_id, name, current_lock)
);

CREATE TABLE tie.ing_engine_job(
   id                     NUMERIC(19, 0)     NOT NULL,
   version                NUMERIC(19, 0)     NOT NULL, 
   assigned               BIGINT             NOT NULL,
   operation              VARCHAR(9)         NOT NULL,
   previous_state         VARCHAR(23)        NOT NULL,
   product_id             NUMERIC(19, 0)     NOT NULL,
   path                   VARCHAR(255)       NULL,
   priority               NUMERIC(10, 0)     NOT NULL CHECK(priority BETWEEN 1 AND 3),
   contribute_storage_id  NUMERIC(19, 0)     NOT NULL,
   
   CONSTRAINT ing_engine_job_pk PRIMARY KEY(id),
   CONSTRAINT ing_engine_job_uq UNIQUE(product_id, operation)  
);

CREATE TABLE tie.ing_federation(
   id        NUMERIC(19, 0)  NOT NULL,
   version   NUMERIC(19, 0)  NOT NULL,
   name      VARCHAR(30)     NOT NULL,
   note      VARCHAR(255)    NOT NULL,
   updated   BIGINT          NULL,
   hostname  VARCHAR(30)     NOT NULL,
   port      NUMERIC(10, 0)  NOT NULL,
   
   CONSTRAINT ing_federation_pk PRIMARY KEY(id),
   CONSTRAINT ing_federation_uq UNIQUE(name)
);

-- CREATE TABLE tie.ing_message_schema(
--   id        NUMERIC(19, 0)  NOT NULL,
--   version   NUMERIC(19, 0)  NOT NULL, 
--   released  BIGINT          NOT NULL,
--   revision  VARCHAR(255)    NOT NULL,
--   SCHEMA    TEXT            NOT NULL,
--   
--   CONSTRAINT ing_message_schema_pk PRIMARY KEY(id),
--   CONSTRAINT ing_message_schema_uq UNIQUE(revision)
--);

CREATE TABLE tie.ing_product(
   id                   NUMERIC(19, 0)     NOT NULL,
   version              NUMERIC(19, 0)     NOT NULL, 
   archive_note         VARCHAR(3000)      NULL,
   archive_text         TEXT               NULL,
   archived_at          BIGINT             NULL,
   complete_text        TEXT               NULL,
   completed            BIGINT             NULL,
   contribute_storage_id NUMERIC(19, 0)    NULL,
   contributor          VARCHAR(255)       NOT NULL,
   created              BIGINT             NULL,
   current_lock         VARCHAR(16)        NULL,
   current_retries      NUMERIC(10, 0)  CHECK(current_retries BETWEEN 0 AND 10)     NOT NULL,
   current_state        VARCHAR(23)        NOT NULL,
   initial_text         TEXT               NULL,
   inventory_id         NUMERIC(19, 0)     NULL,
   local_relative_path  VARCHAR(255)       NULL,
   name                 VARCHAR(125)       NOT NULL,
   note                 VARCHAR(3000)      NULL,
   notify               BOOLEAN            NOT NULL,
   product_type_id      NUMERIC(19, 0)     NOT NULL,
   readers              NUMERIC(10, 0)     NULL,
   remote_relative_path VARCHAR(255)       NULL,
--   schema_id            NUMERIC(19, 0)     NULL,
   updated              BIGINT             NULL,
   version_number       NUMERIC(10, 0)     NOT NULL,
   version_string       VARCHAR(255)       NULL,

   CONSTRAINT ing_product_pk PRIMARY KEY(id),
   CONSTRAINT ing_product_uq UNIQUE(product_type_id, name, version_string)
);

CREATE TABLE tie.ing_event_category (
   id                   NUMERIC(19, 0)  NOT NULL,
   version              NUMERIC(19, 0)  NOT NULL, 
   name                 VARCHAR(100)    NOT NULL,

   CONSTRAINT ing_event_category_uq UNIQUE(name),
   CONSTRAINT ing_event_category_pk PRIMARY KEY(id)
);

CREATE TABLE tie.ing_product_type(
   id                 NUMERIC(19, 0)  NOT NULL,
   version            NUMERIC(19, 0)  NOT NULL,
   federation_id      NUMERIC(19, 0)  NOT NULL,
   ingest_only        BOOLEAN         NOT NULL,
   locked             BOOLEAN         NOT NULL,
   locked_at          BIGINT          NULL,
   locked_by          VARCHAR(255)    NULL,
   name               VARCHAR(100)    NOT NULL,
   note               VARCHAR(255)    NULL,
   purge_rate         NUMERIC(10, 0)  NOT NULL,
   local_staged       BOOLEAN         NOT NULL,
   relative_path      VARCHAR(255)    NOT NULL,
   updated            BIGINT          NOT NULL,
   updated_by_id      NUMERIC(19, 0)  NOT NULL,
   event_category_id  NUMERIC(19, 0)  NULL,
   delivery_rate      NUMERIC(10, 0)  NULL,
   priority           NUMERIC(10, 0)  NOT NULL CHECK(priority BETWEEN 1 AND 3),
   
   
   CONSTRAINT ing_product_type_pk PRIMARY KEY(id),
   CONSTRAINT ing_product_type_uq UNIQUE(federation_id, name)
);

CREATE TABLE tie.ing_product_type_role(
   id              NUMERIC(19, 0) NOT NULL,
   version         NUMERIC(19, 0) NOT NULL,
   product_type_id NUMERIC(19, 0) NOT NULL,
   role_id         NUMERIC(19, 0) NOT NULL,
   
   CONSTRAINT ing_product_type_role_pk PRIMARY KEY(id),
   CONSTRAINT ing_proudct_type_role_uq UNIQUE(product_type_id, role_id)
);

CREATE TABLE tie.ing_remote_system(
   id              NUMERIC(19, 0)  NOT NULL,
   version         NUMERIC(19, 0)  NOT NULL, 
   created         BIGINT          NULL,
   max_connections NUMERIC(10, 0)  NOT NULL,
   note            VARCHAR(255)    NULL,
   organization    VARCHAR(255)    NOT NULL,
   password        VARCHAR(255)    NULL,
   root_uri        VARCHAR(255)    NOT NULL,
   updated         BIGINT          NULL,
   updated_by      VARCHAR(255)    NULL,
   username        VARCHAR(255)    NULL,
   
   CONSTRAINT ing_remote_system_pk PRIMARY KEY(id),
   CONSTRAINT ing_remote_system_uq UNIQUE(root_uri)
);

CREATE TABLE tie.ing_storage(
   id                     NUMERIC(19, 0)  NOT NULL,
   version                NUMERIC(19, 0)  NOT NULL,
   created                BIGINT          NULL,
   name                   VARCHAR(255)    NOT NULL,
   priority               NUMERIC(10, 0)  CHECK(priority BETWEEN 1 AND 3),
   location_id            NUMERIC(19, 0)  NOT NULL,
   
   CONSTRAINT ing_storage_pk PRIMARY KEY(id),
   CONSTRAINT ing_storage_uq UNIQUE(name)  
);

CREATE TABLE tie.ing_location(
   id                     NUMERIC(19, 0)  NOT NULL,
   version                NUMERIC(19, 0)  NOT NULL,
   created                BIGINT          NULL,
   last_used              BIGINT          NULL,
   local_path             VARCHAR(255)    NOT NULL,
   remote_access_protocol VARCHAR(5)      NULL,
   remote_path            VARCHAR(255)    NOT NULL,
   space_reserved         NUMERIC(19, 0)  NOT NULL,
   space_threshold        NUMERIC(19, 0)  NULL,
   space_used             NUMERIC(19, 0)  NULL,
   stereotype             VARCHAR(30)     NOT NULL,
   hostname               VARCHAR(30)     NOT NULL,
   active                 BOOLEAN         NOT NULL,
   
   CONSTRAINT ing_location_pk PRIMARY KEY(id),
   CONSTRAINT ing_location_uq UNIQUE(local_path)  
);

CREATE TABLE tie.ing_system_user(
   id         NUMERIC(19, 0)  NOT NULL,
   version    NUMERIC(19, 0)  NOT NULL,
   admin      BOOLEAN         NOT NULL,
   email      VARCHAR(255)    NOT NULL,
   fullname   VARCHAR(40)     NOT NULL,
   name       VARCHAR(180)    NOT NULL,
   note       VARCHAR(255)    NULL,
   password   VARCHAR(40)     NOT NULL,
   read_all   BOOLEAN         NOT NULL,
   write_all  BOOLEAN         NOT NULL,

   CONSTRAINT ing_system_user_pk PRIMARY KEY(id),
   CONSTRAINT ing_system_user_uq UNIQUE(name)
);

CREATE TABLE tie.ing_system_user_role(
   id       NUMERIC(19, 0)  NOT NULL,
   version  NUMERIC(19, 0)  NOT NULL,
   role_id  NUMERIC(19, 0)  NOT NULL,
   user_id  NUMERIC(19, 0)  NOT NULL,

   CONSTRAINT ing_system_user_role_pk PRIMARY KEY(id),
   CONSTRAINT ing_system_user_role_uq UNIQUE(user_id, role_id) 
);

CREATE TABLE tie.ing_system_user_session(
   id               NUMERIC(19, 0)  NOT NULL,
   version          NUMERIC(19, 0)  NOT NULL,
   expire_time      BIGINT          NULL,
   issue_time       BIGINT          NOT NULL,
   product_type_id  NUMERIC(19, 0)  NULL,
   token            VARCHAR(255)    NOT NULL,
   user_id          NUMERIC(19, 0)  NOT NULL,
   
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

--ALTER TABLE tie.ing_product ADD 
--   CONSTRAINT ing_product_fk2 
--   FOREIGN KEY(schema_id) 
--   REFERENCES tie.ing_message_schema(id);

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

-- CREATE SEQUENCE tie.hibernate_sequence
--   CACHE 1;
CREATE SEQUENCE tie.ing_federation_id_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_systemuser_id_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_systemusersession_id_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_accessrole_id_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_systemuserrole_id_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_producttype_id_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_producttyperole_id_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_product_id_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_product_version_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_data_file_id_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_storage_id_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_enginejob_id_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_remotesystem_id_seq
   CACHE 1;
-- CREATE SEQUENCE tie.ing_messageschema_id_seq
--   CACHE 1;
CREATE SEQUENCE tie.ing_event_category_id_seq
   CACHE 1;
CREATE SEQUENCE tie.ing_location_id_seq
   CACHE 1;
   
