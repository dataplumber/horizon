--
-- Copyright (c) 2013, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script drops the database objects for the Sigevent domain schema.
--

DROP TABLE IF EXISTS tie.sys_event_group CASCADE;
DROP TABLE IF EXISTS tie.sys_event CASCADE;
DROP TABLE IF EXISTS tie.sys_notify CASCADE;
DROP TABLE IF EXISTS tie.sys_outgoing CASCADE;
DROP TABLE IF EXISTS tie.sys_role CASCADE;
DROP TABLE IF EXISTS tie.sys_user CASCADE;
DROP TABLE IF EXISTS tie.sys_user_setting CASCADE;

DROP SEQUENCE IF EXISTS tie.sys_event_group_id_seq;
DROP SEQUENCE IF EXISTS tie.sys_event_id_seq;
DROP SEQUENCE IF EXISTS tie.sys_notify_id_seq;
DROP SEQUENCE IF EXISTS tie.sys_outgoing_id_seq;
DROP SEQUENCE IF EXISTS tie.sys_role_id_seq;
DROP SEQUENCE IF EXISTS tie.sys_user_id_seq;
DROP SEQUENCE IF EXISTS tie.sys_user_setting_id_seq;
