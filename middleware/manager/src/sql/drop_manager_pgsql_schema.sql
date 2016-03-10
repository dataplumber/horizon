--
-- Copyright (c) 2013, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script drops the database objects for the DMAS Manager schema.
--

DROP TABLE IF EXISTS tie.ing_access_role CASCADE;
DROP TABLE IF EXISTS tie.ing_data_file CASCADE;
DROP TABLE IF EXISTS tie.ing_engine_job CASCADE;
DROP TABLE IF EXISTS tie.ing_federation CASCADE;
DROP TABLE IF EXISTS tie.ing_message_schema CASCADE;
DROP TABLE IF EXISTS tie.ing_product CASCADE;
DROP TABLE IF EXISTS tie.ing_event_category CASCADE;
DROP TABLE IF EXISTS tie.ing_product_type CASCADE;
DROP TABLE IF EXISTS tie.ing_product_type_role CASCADE;
DROP TABLE IF EXISTS tie.ing_remote_system CASCADE;
DROP TABLE IF EXISTS tie.ing_storage CASCADE;
DROP TABLE IF EXISTS tie.ing_system_user CASCADE;
DROP TABLE IF EXISTS tie.ing_system_user_role CASCADE;
DROP TABLE IF EXISTS tie.ing_system_user_session CASCADE;
DROP TABLE IF EXISTS tie.ing_location CASCADE;
DROP SEQUENCE IF EXISTS tie.hibernate_sequence;
DROP SEQUENCE IF EXISTS tie.ing_federation_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_product_version_seq;
DROP SEQUENCE IF EXISTS tie.ing_systemuser_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_accessrole_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_systemuserrole_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_systemusersession_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_producttype_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_producttyperole_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_product_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_data_file_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_storage_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_enginejob_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_remotesystem_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_messageschema_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_event_category_id_seq;
DROP SEQUENCE IF EXISTS tie.ing_location_id_seq;
