--
-- Copyright (c) 2013, by the California Institute of Technology.
-- ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
--
-- $Id: $
--

--
-- This SQL script creates the database objects for the DMAS Inventory schema.
--

CREATE TABLE tie.realm
(
  id bigint NOT NULL,
  version bigint NOT NULL,
  description character varying(255) NOT NULL,
  name character varying(255) NOT NULL,
  token_expiration integer NOT NULL,
  verifier_id bigint NOT NULL,
  CONSTRAINT realm_pkey PRIMARY KEY (id)
);

CREATE TABLE tie.role
(
  id bigint NOT NULL,
  version bigint NOT NULL,
  realm_id bigint NOT NULL,
  role_group character varying(255) NOT NULL,
  role_name character varying(255) NOT NULL,
  CONSTRAINT role_pkey PRIMARY KEY (id)
);

CREATE TABLE tie.token
(
  id bigint NOT NULL,
  version bigint NOT NULL,
  create_date bigint NOT NULL,
  realm_id bigint NOT NULL,
  token character varying(255) NOT NULL,
  user_name character varying(255) NOT NULL,
  CONSTRAINT token_pkey PRIMARY KEY (id)
);

CREATE TABLE tie.verifier
(
  id bigint NOT NULL,
  version bigint NOT NULL,
  name character varying(255) NOT NULL,
  CONSTRAINT verifier_pkey PRIMARY KEY (id)
);

ALTER TABLE tie.role ADD 
   CONSTRAINT role_fk1
   FOREIGN KEY(realm_id) 
   REFERENCES tie.realm(id);

ALTER TABLE tie.realm ADD 
   CONSTRAINT realm_fk1
   FOREIGN KEY(verifier_id) 
   REFERENCES tie.verifier(id);

ALTER TABLE tie.token ADD 
   CONSTRAINT token_fk1
   FOREIGN KEY(realm_id) 
   REFERENCES tie.realm(id);

CREATE SEQUENCE tie.realm_id_seq
   CACHE 1;

CREATE SEQUENCE tie.role_id_seq
   CACHE 1;

CREATE SEQUENCE tie.token_id_seq
   CACHE 1;

CREATE SEQUENCE tie.verifier_id_seq
   CACHE 1;
