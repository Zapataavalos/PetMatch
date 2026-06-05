# PetMatch Agent Rules

- Do not modify business logic without explicit authorization.
- Do not modify frontend files for Docker tasks.
- For Docker/build tasks, change only Dockerfile, docker-compose.yml, .dockerignore, .gitignore, AGENTS.md, or pom.xml when the Maven build requires it.
- Validate Compose changes with `docker compose config`.
- Validate individual services with `docker compose build <service> --no-cache --progress=plain`.
- Do not version `target/`, `*.jar`, `node_modules/`, `dist/`, or `.env`.
- Keep Dockerfiles multi-stage and do not use `dependency:go-offline`.
