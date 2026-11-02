# Software Engineers API

**A small REST API with Spring Boot, JPA and PostgreSQL.**

By [Nikita Kozchenko](https://github.com/Nk0shey) · Java 17 · Spring Boot 3.4.5 · PostgreSQL · Maven

[Quick start](#quick-start) · [API](#api) · [Tests](#tests) · [Review](docs/REVIEW.md)

The application lives in `spring-boot-1/`. It stores software engineers with a generated ID, name and technology stack. Controllers delegate to a service and Spring Data repository.

## Quick start

Requires a JDK 17+, Maven 3.9+ and Docker Compose for the local PostgreSQL database.

```sh
cd spring-boot-1
docker compose up -d --wait
mvn spring-boot:run
```

The API listens on `http://localhost:8080`. PostgreSQL is exposed on local port 5332. Compose explicitly creates `nk0sheycode`, matching the application's default JDBC URL.

The repository includes Maven Wrapper configuration. As an alternative to an installed Maven, use `sh ./mvnw spring-boot:run` on macOS/Linux or `mvnw.cmd spring-boot:run` on Windows. Wrapper startup was checked on Linux.

### Configuration

The defaults are for local demonstration. Override application settings through environment variables:

- `DB_URL`: defaults to `jdbc:postgresql://localhost:5332/nk0sheycode`.
- `DB_USER`: defaults to `nk0shey`.
- `DB_PASSWORD`: defaults to `local-development-only`.
- `DDL_AUTO`: defaults to `update`, preserving records across restarts.
- `SHOW_SQL`: defaults to `false`.

`.env.example` documents Compose settings. Compose reads a copied `.env` file; the Java process does not automatically read that file, so custom credentials must also be exported into its environment. `DB_NAME` configures the container's database; update `DB_URL` correspondingly if you change it. Existing volumes initialized with different database settings are not migrated automatically.

Stop the database without deleting its volume:

```sh
docker compose down
```

## API

- `GET /api/v1/software-engineers`: list records.
- `GET /api/v1/software-engineers/{id}`: find a record; unknown IDs return 404, nonpositive IDs return 400.
- `POST /api/v1/software-engineers`: create a record; returns 201, a `Location` header and the created JSON.

Create an example:

```sh
curl -i -X POST http://localhost:8080/api/v1/software-engineers \
  -H 'Content-Type: application/json' \
  -d '{"name":"Demo Engineer","techStack":"Java, Spring Boot"}'
```

Read records:

```sh
curl http://localhost:8080/api/v1/software-engineers
curl http://localhost:8080/api/v1/software-engineers/1
```

Replace `1` with the generated ID from the creation response. Names and stacks must be nonblank; maximum lengths are 100 and 500 characters. Clients cannot supply an existing ID to overwrite a record. Additional examples are in `spring-boot-1/requests.http`.

## Tests

From `spring-boot-1/`:

```sh
mvn test
mvn package
```

Seven integration tests run the Spring context, HTTP controller layer and JPA repository against an isolated H2 database in PostgreSQL compatibility mode. Docker/PostgreSQL is not needed for tests. The test Mockito configuration avoids requiring a dynamically attached JVM agent.

Verified on Linux/OpenJDK 17: all seven tests pass and executable-JAR packaging succeeds. PostgreSQL/Docker startup was not executed in this environment, so H2 results do not establish full PostgreSQL compatibility. The API currently implements list, read and create operations. See [review notes](docs/REVIEW.md) for changes and limits.
