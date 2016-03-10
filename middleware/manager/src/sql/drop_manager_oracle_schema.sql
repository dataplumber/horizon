--
-- Copyright (c) 2008, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: drop_ingest_oracle_schema.sql 2470 2009-01-21 01:53:43Z thuang $
--

--
-- This SQL script drops the database objects for the Horizon domain schema.
--

DROP TABLE ing_access_role cascade CONSTRAINTS PURGE;
DROP TABLE ing_data_file cascade CONSTRAINTS PURGE;
DROP TABLE ing_engine_job cascade CONSTRAINTS PURGE;
DROP TABLE ing_federation cascade CONSTRAINTS PURGE;
DROP TABLE ing_message_schema cascade CONSTRAINTS PURGE;
DROP TABLE ing_product cascade CONSTRAINTS PURGE;
DROP TABLE ing_event_category cascade CONSTRAINTS PURGE;
DROP TABLE ing_product_type cascade CONSTRAINTS PURGE;
DROP TABLE ing_product_type_role cascade CONSTRAINTS PURGE;
DROP TABLE ing_remote_system cascade CONSTRAINTS PURGE;
DROP TABLE ing_storage cascade CONSTRAINTS PURGE;
DROP TABLE ing_system_user cascade CONSTRAINTS PURGE;
DROP TABLE ing_system_user_role cascade CONSTRAINTS PURGE;
DROP TABLE ing_system_user_session cascade CONSTRAINTS PURGE;
DROP TABLE ing_location cascade CONSTRAINTS PURGE;
DROP SEQUENCE hibernate_sequence;
DROP SEQUENCE ing_federation_id_seq;
DROP SEQUENCE ing_product_version_seq;
DROP SEQUENCE ing_systemuser_id_seq;
DROP SEQUENCE ing_accessrole_id_seq;
DROP SEQUENCE ing_systemuserrole_id_seq;
DROP SEQUENCE ing_systemusersession_id_seq;
DROP SEQUENCE ing_producttype_id_seq;
DROP SEQUENCE ing_producttyperole_id_seq;
DROP SEQUENCE ing_product_id_seq;
DROP SEQUENCE ing_data_file_id_seq;
DROP SEQUENCE ing_storage_id_seq;
DROP SEQUENCE ing_enginejob_id_seq;
DROP SEQUENCE ing_remotesystem_id_seq;
DROP SEQUENCE ing_messageschema_id_seq;
DROP SEQUENCE ing_event_category_id_seq;
DROP SEQUENCE ing_location_id_seq;
