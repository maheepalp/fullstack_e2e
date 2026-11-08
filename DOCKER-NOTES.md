# Docker notes: run the app locally

This runs the React frontend and Spring Boot backend in two new containers. It keeps the existing `learning-postgres` container and its data unchanged.

## 1. Prerequisites

Start Docker Desktop. From the project root (`C:\development\fullstack_e2e`), check that PostgreSQL is running and Compose is available:

```powershell
docker compose version
docker ps
```

The existing PostgreSQL container should be named `learning-postgres` and publish port 5432. If it is stopped, start that container only:

```powershell
docker start learning-postgres
```

## 2. Configure the local database password

Copy the Docker-specific example and edit the password if your `expenses_app` password is different:

```powershell
Copy-Item .\.env.docker.example .\.env
notepad .\.env
```

Set `DB_PASSWORD` to the password for the app's PostgreSQL login. The `.env` file is local and Git-ignored; do not commit it. The backend container reaches PostgreSQL at `host.docker.internal:5432`. Do not use `localhost` for this JDBC URL: inside a container, `localhost` means that same container.

## 3. Build and start the two app containers

Run these commands from the project root:

```powershell
docker compose config --quiet
docker compose up --build -d
docker compose ps
```

The frontend is available at [http://localhost:3000](http://localhost:3000). Nginx serves the built React app and proxies `/api` calls to the backend container. The frontend build excludes local `.env` files and uses `/api` as its API base URL.

The backend stays private on the Compose network; the browser reaches it through the frontend proxy. Test the API via `http://localhost:3000/api/expenses`.

## 4. Check logs and functionality

Follow backend startup logs:

```powershell
docker compose logs -f backend
```

Press `Ctrl+C` to stop following logs; this does not stop the containers. Check the API and frontend responses:

```powershell
curl.exe -i http://localhost:3000/api/expenses
curl.exe -i http://localhost:3000/
```

The API should return HTTP 200 and JSON (an empty database returns `[]`). Then use the frontend to create, search, edit, and delete an expense. Confirm persistence using the existing database:

```powershell
docker exec -it learning-postgres psql -h localhost -U expenses_app -d fullstack_e2e -W -c "SELECT id, expense_date, category, description, amount FROM expenses ORDER BY expense_date DESC, id DESC;"
```

## 5. Stop or rebuild

Stop just the two app containers:

```powershell
docker compose down
```

Start them again without rebuilding:

```powershell
docker compose up -d
```

Rebuild after changing source or Docker configuration:

```powershell
docker compose up --build -d
```

`docker compose down` does not stop or delete `learning-postgres`. Do not use `docker compose down -v` for this project.
