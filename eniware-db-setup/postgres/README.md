# EniwareNet PostgreSQL Database Setup

SolarNet requires a [PostgreSQL][pgsql] database, version 9.6 or higher, to
operate. Please note that only version 9.6 has been extensively tested. It also
requires the [plv8][plv8] extension.

## Requirements

 * PostgreSQL 9.6 - http://www.postgresql.org/
 * plv8 extension 1.4 - http://pgxn.org/dist/plv8/doc/plv8.html
 * citext extension (from PostgreSQL contrib)

## Create database

The `postgres-create.sql` script can be used to

 1. Create a **eniware** database user.
 2. Create a **eniware** database, owned by the **eniware** user.
 3. Install the **citext** and **pgcrypto** extensions.

This script should be run as a database superuser, for example **postgres**.
Assuming Postgres is available on the same machine you are are, the script can
be executed similarly to this:

```shell
$ psql -U postgres -f postgres-create.sql
```


## Setup plv8

The plv8 configuration will be owned by the database superuser in this setup,
not the SolarNet database user, as it contains cluster-wide elements. Run the
`postgres-init-plv8.sql` script as the superuser:

```shell
$ psql -U postgres -d eniware -f postgres-init-plv8.sql
```

Now the `postgresql.conf` configuration must be updated to add the plv8 global
context. Add a line like the following:

	plv8.start_proc = 'plv8_startup'

For more information on how plv8 is used in SolarNet, see the [plv8 module
project][plv8-proj].

## Setup database

Now the SolarNet database schemas and tables can be created, this time as the
normal database user:

```shell
$ psql -U eniware -d eniware -f postgres-init.sql
```

## Done

The database setup is now complete.

  [pgsql]: http://www.postgresql.org/
  [plv8]: http://pgxn.org/dist/plv8/doc/plv8.html
  [plv8-proj]: plv8/
