--
-- Copyright (c) 2009, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script drops the database objects for the Sigevent domain schema.
--

DROP TABLE sys_event_group cascade CONSTRAINTS PURGE;
DROP TABLE sys_event cascade CONSTRAINTS PURGE;
DROP TABLE sys_notify cascade CONSTRAINTS PURGE;
DROP TABLE sys_outgoing cascade CONSTRAINTS PURGE;
DROP TABLE sys_role cascade CONSTRAINTS PURGE;
DROP TABLE sys_user cascade CONSTRAINTS PURGE;

DROP SEQUENCE sys_event_group_id_seq;
DROP SEQUENCE sys_event_id_seq;
DROP SEQUENCE sys_notify_id_seq;
DROP SEQUENCE sys_outgoing_id_seq;
DROP SEQUENCE sys_role_id_seq;
DROP SEQUENCE sys_user_id_seq;
