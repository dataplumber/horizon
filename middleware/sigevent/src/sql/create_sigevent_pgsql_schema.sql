--
-- Copyright (c) 2013, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script creates the database objects for the Sigevent domain schema.
--


CREATE TABLE tie.sys_event_group (
   id         BIGINT                  NOT NULL,
   version    BIGINT                  NOT NULL,
   type       CHARACTER VARYING(5)    NOT NULL,
   category   CHARACTER VARYING(100)  NOT NULL,
   purge_rate BIGINT                  NOT NULL,
   
   CONSTRAINT sys_event_group_pk PRIMARY KEY(id),
   CONSTRAINT sys_event_group_uq UNIQUE (type, category)
);

CREATE TABLE tie.sys_event (
   id             BIGINT                  NOT NULL,
   version        BIGINT                  NOT NULL,
   group_id       BIGINT                  NOT NULL,
   first_received BIGINT                  NOT NULL,
   last_received  BIGINT                  NOT NULL,
   occurrence     BIGINT                  NOT NULL,
   source         CHARACTER VARYING(40)   NOT NULL,
   provider       CHARACTER VARYING(40)   NOT NULL,
   computer       CHARACTER VARYING(40)   NOT NULL,
   pid            INTEGER                 NULL,
   description    CHARACTER VARYING(256)  NOT NULL,
   data           TEXT                    NULL,
   resolution     CHARACTER VARYING(256)  NOT NULL,
   resolved_at    BIGINT                  NULL,
   
   CONSTRAINT sys_event_pk PRIMARY KEY (id),
   CONSTRAINT sys_event_fk FOREIGN KEY (group_id) 
      REFERENCES tie.sys_event_group(id)
);

CREATE TABLE tie.sys_notify (
   id             BIGINT                  NOT NULL,
   version        BIGINT                  NOT NULL,
   group_id       BIGINT                  NOT NULL,
   method         CHARACTER VARYING(9)    NOT NULL,
   contact        CHARACTER VARYING(40)   NOT NULL,
   rate           BIGINT                  NOT NULL,
   remind_rate    INTEGER                 NULL,
   message_format CHARACTER VARYING(5)    NOT NULL,
   content        CHARACTER VARYING(11)   NOT NULL,
   last_report    BIGINT                  NULL,
   last_notified  BIGINT                  NULL,
   last_remind    BIGINT                  NULL,
   note           CHARACTER VARYING(256)  NULL,
   
   CONSTRAINT sys_notify_pk PRIMARY KEY (id),
   CONSTRAINT sys_notify_fk FOREIGN KEY (group_id)
      REFERENCES tie.sys_event_group(id)
);

CREATE TABLE tie.sys_outgoing (
   id           BIGINT                  NOT NULL,
   version      BIGINT                  NOT NULL,
   type         CHARACTER VARYING(5)    NOT NULL,
   category     CHARACTER VARYING(40)   NOT NULL,
   method       CHARACTER VARYING(9)    NOT NULL,
   contact      CHARACTER VARYING(40)   NOT NULL,
   created      BIGINT                  NOT NULL,
   message      TEXT                    NOT NULL,
   notify_id    BIGINT                  NULL,
   message_type CHARACTER VARYING(8)    NOT NULL,
   
   CONSTRAINT sys_outgoing_pk PRIMARY KEY (id)
);

CREATE TABLE tie.sys_role (
   id       BIGINT                  NOT NULL,
   version  BIGINT                  NOT NULL,
   name     CHARACTER VARYING(100)  NOT NULL,
   admin    INTEGER                 NOT NULL,
   
   CONSTRAINT sys_role_pk PRIMARY KEY (id),
   CONSTRAINT sys_role_uq UNIQUE (name)
);

CREATE TABLE tie.sys_user (
   id       BIGINT                  NOT NULL,
   version  BIGINT                  NOT NULL,
   username CHARACTER VARYING(40)   NOT NULL,
   password CHARACTER VARYING(100)  NOT NULL,
   role_id  BIGINT                  NOT NULL,
   
   CONSTRAINT sys_user_pk PRIMARY KEY (id),
   CONSTRAINT sys_user_uq UNIQUE (username),
   CONSTRAINT sys_user_fk FOREIGN KEY (role_id)
      REFERENCES tie.sys_role(id)
);

CREATE TABLE tie.sys_user_setting (
   id       BIGINT                  NOT NULL,
   version  BIGINT                  NOT NULL,
   username CHARACTER VARYING(40)   NOT NULL,
   setting  CHARACTER VARYING(255)  NULL,
   
   CONSTRAINT sys_user_setting_pk PRIMARY KEY (id),
   CONSTRAINT sys_user_setting_uq UNIQUE (username)
);

CREATE SEQUENCE tie.sys_event_group_id_seq
   CACHE 1;

CREATE SEQUENCE tie.sys_event_id_seq
   CACHE 1;

CREATE SEQUENCE tie.sys_notify_id_seq
   CACHE 1;

CREATE SEQUENCE tie.sys_outgoing_id_seq
   CACHE 1;

CREATE SEQUENCE tie.sys_user_id_seq
   CACHE 1;

CREATE SEQUENCE tie.sys_user_setting_id_seq
   CACHE 1;

CREATE SEQUENCE tie.sys_role_id_seq
   CACHE 1;
