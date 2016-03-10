--
-- Copyright (c) 2013, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script drops the database objects for the DMAS Inventory schema.
--

DROP TABLE IF EXISTS tie.realm CASCADE;
DROP TABLE IF EXISTS tie.role CASCADE;
DROP TABLE IF EXISTS tie.token CASCADE;
DROP TABLE IF EXISTS tie.verifier CASCADE;

DROP SEQUENCE IF EXISTS tie.realm_id_seq;
DROP SEQUENCE IF EXISTS tie.role_id_seq;
DROP SEQUENCE IF EXISTS tie.token_id_seq;
DROP SEQUENCE IF EXISTS tie.verifier_id_seq;
