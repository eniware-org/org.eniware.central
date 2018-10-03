/* ============================================================================
 * Sample script to create a new user and database for the eniwarenet
 * application. Use this as a guide only, and modify names and passwords as
 * appropriate.
 * ============================================================================
 */
CREATE ROLE eniware LOGIN ENCRYPTED PASSWORD 'Eniware8' VALID UNTIL 'infinity';

CREATE DATABASE eniware WITH ENCODING='UTF8' OWNER=eniware TEMPLATE=template0;

\connect eniware

-- pgpgsql is included by default in Postgres 9.x now
-- CREATE LANGUAGE plpgsql;

CREATE EXTENSION IF NOT EXISTS citext WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;
