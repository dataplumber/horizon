--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script creates the database objects for the Sigevent domain schema.
--


CREATE TABLE tie.sys_event_group (
   id         NUMBER(19, 0)     NOT NULL,
   version    NUMBER(19, 0)     NOT NULL,
   category   VARCHAR(100) NOT NULL,
   purge_rate NUMBER(19, 0)     NOT NULL,
   type       VARCHAR(12) NOT NULL,
   
   CONSTRAINT sys_event_group_pk PRIMARY KEY(id),
   CONSTRAINT sys_event_group_uq UNIQUE (type, category)
);

CREATE TABLE tie.sys_event (
   id             NUMBER(19, 0)      NOT NULL,
   version        NUMBER(19, 0)      NOT NULL,
   computer       VARCHAR(40)  NOT NULL,
   data           CLOB               NULL,
   description    VARCHAR(256) NOT NULL,
   first_received NUMBER             NOT NULL,
   group_id       NUMBER(19, 0)      NOT NULL,
   last_received  NUMBER             NOT NULL,
   occurrence     NUMBER(19, 0)      NOT NULL,
   pid            NUMBER(19, 0)      NULL,
   provider       VARCHAR(40)  NOT NULL,
   resolution     VARCHAR(256) NOT NULL,
   resolved_at    NUMBER             NULL,
   source         VARCHAR(40)  NOT NULL,
   
   CONSTRAINT sys_event_pk PRIMARY KEY (id),
   CONSTRAINT sys_event_fk FOREIGN KEY (group_id) 
      REFERENCES tie.sys_event_group(id)
);

CREATE TABLE tie.sys_notify (
   id             NUMBER(19, 0)      NOT NULL,
   version        NUMBER(19, 0)      NOT NULL,
   contact        VARCHAR(40)  NOT NULL,
   group_id       NUMBER(19, 0)      NOT NULL,
   last_notified  NUMBER             NULL,
   last_report    NUMBER             NULL,
   last_remind    NUMBER             NULL,
   message_format VARCHAR(10)  NOT NULL,
   content        VARCHAR(11)  NOT NULL,
   method         VARCHAR(20)  NOT NULL,
   note           VARCHAR(256)  NULL,
   rate           NUMBER(19, 0)      NOT NULL,
   remind_rate    NUMBER(10, 0)      NULL,
   
   CONSTRAINT sys_notify_pk PRIMARY KEY (id),
   CONSTRAINT sys_notify_fk FOREIGN KEY (group_id)
      REFERENCES tie.sys_event_group(id)
);

CREATE TABLE tie.sys_outgoing (
   id       NUMBER(19, 0)       NOT NULL,
   version  NUMBER(19, 0)       NOT NULL,
   category VARCHAR(40)   NOT NULL,
   contact  VARCHAR(40)   NOT NULL,
   created  NUMBER              NOT NULL,
   message  CLOB                NOT NULL,
   method   VARCHAR(20)   NOT NULL,
   type     VARCHAR(12)   NOT NULL,
   notify_id NUMBER(19, 0),
   message_type VARCHAR(8) NOT NULL,
   
   CONSTRAINT sys_outgoing_pk PRIMARY KEY (id)
);

CREATE TABLE tie.sys_role (
   id       NUMBER(19, 0)       NOT NULL,
   version  NUMBER(19, 0)       NOT NULL,
   name VARCHAR(100)   NOT NULL,
   admin  NUMBER(1, 0)       NOT NULL,
   
   CONSTRAINT sys_role_pk PRIMARY KEY (id),
   CONSTRAINT sys_role_uq UNIQUE (name)
);

CREATE TABLE tie.sys_user (
   id       NUMBER(19, 0)       NOT NULL,
   version  NUMBER(19, 0)       NOT NULL,
   username VARCHAR(40)   NOT NULL,
   password  VARCHAR(100)   NOT NULL,
   role_id  NUMBER(19, 0)       NOT NULL,
   
   CONSTRAINT sys_user_pk PRIMARY KEY (id),
   CONSTRAINT sys_user_uq UNIQUE (username),
   CONSTRAINT sys_user_fk FOREIGN KEY (role_id)
      REFERENCES tie.sys_role(id)
);

CREATE TABLE tie.sys_user_setting (
   id       NUMBER(19, 0)       NOT NULL,
   version  NUMBER(19, 0)       NOT NULL,
   username VARCHAR(40)   NOT NULL,
   setting  CLOB,
   
   CONSTRAINT sys_user_setting_pk PRIMARY KEY (id),
   CONSTRAINT sys_user_setting_uq UNIQUE (username)
);

CREATE SEQUENCE tie.sys_event_group_id_seq
   NOCACHE;

CREATE SEQUENCE tie.sys_event_id_seq
   NOCACHE;

CREATE SEQUENCE tie.sys_notify_id_seq
   NOCACHE;

CREATE SEQUENCE tie.sys_outgoing_id_seq
   NOCACHE;

CREATE SEQUENCE tie.sys_user_id_seq
   NOCACHE;

CREATE SEQUENCE tie.sys_user_setting_id_seq
   NOCACHE;

CREATE SEQUENCE tie.sys_role_id_seq
   NOCACHE;
