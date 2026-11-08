# Personal Expense Tracker

A small full-stack learning application for recording and managing personal expenses. The React frontend calls a Spring Boot REST API, which persists records to PostgreSQL.

## Technology stack

- Frontend: React, JavaScript, Vite, Fetch API, CSS
- Backend: Java 21, Spring Boot 3.5, Spring MVC, Spring Data JPA, Bean Validation, Maven
- Database: PostgreSQL 16 (reuses your existing Docker container)

## Project structure

```text
backend/   Spring Boot REST API and automated tests
frontend/  React single-page application
README.md  Local setup and usage instructions
```

## Prerequisites

Install Java 21, Maven 3.9+, Node.js 20+, npm, and Docker Desktop. The existing PostgreSQL container must expose port 5432.

## Database setup

The existing `learning-postgres` container has already been configured for this application: database `fullstack_e2e`, login `expenses_app`, and development password `expenses_app_pwd`. The app login is not a PostgreSQL administrator. It has access only to the new application database. The existing `notesdb` database and `notes` login are separate and are not used by the app.

PostgreSQL has no universal default password. In this container the `postgres` role does not exist. We verified that the pre-existing `notes` role is a superuser and can connect locally from inside the container without a password prompt; it was used to create the app database and login. Do not reset or change the `notes` password for this application.

Verify the app login from PowerShell (enter `expenses_app_pwd` at the password prompt):

```powershell
docker exec -it learning-postgres psql -h localhost -U expenses_app -d fullstack_e2e -W -c "SELECT current_database(), current_user;"
```

The backend defaults to `jdbc:postgresql://localhost:5432/fullstack_e2e`. Hibernate manages the `expenses` table with `spring.jpa.hibernate.ddl-auto=update`; a database check constraint rejects non-positive amounts.

Spring Boot does **not** load `.env.example` automatically. Starting at the project root (`C:\development\fullstack_e2e`), load its variables and change to the backend folder in the same PowerShell window:

```powershell
Get-Content .\.env.example | ForEach-Object {
    if ($_ -match '^\s*([A-Za-z_][A-Za-z0-9_]*)=(.*)$') {
        Set-Item -Path "Env:$($matches[1])" -Value $matches[2]
    }
}
Set-Location .\backend
mvn spring-boot:run
```

Alternatively, set `$env:DB_URL`, `$env:DB_USERNAME`, and `$env:DB_PASSWORD` manually in that PowerShell window before running Maven. Environment variables set in a different terminal window are not inherited.

Confirm the existing container and port:

```powershell
docker ps
Test-NetConnection localhost -Port 5432
```

If the existing container is stopped, start only that container:

```powershell
docker start learning-postgres
```

The app database and login already exist, so do not run the creation commands again. If you need to inspect this container's local administrator access, run this read-only command:

```powershell
docker exec learning-postgres psql -U notes -d postgres -c "SELECT current_user, rolsuper FROM pg_roles WHERE rolname = current_user;"
```

It should report `notes` and `t`. To create the app database on a newly initialized container, first identify an authorized administrator. You can inspect the container's initial environment locally with:

```powershell
docker inspect --format '{{range .Config.Env}}{{println .}}{{end}}' learning-postgres | Select-String '^POSTGRES_(USER|PASSWORD|DB)='
```

Those variables, if present, are initialization settings and may not disclose a password that was changed later. PostgreSQL does not provide a way to recover an existing password in plaintext. Do not reset an existing account or delete the container/volume to recover access; first confirm which other apps depend on it. If an authorized administrator is available, create only the new app role and database, after checking whether they already exist.

Verify saved data directly:

```powershell
docker exec -it learning-postgres psql -h localhost -U expenses_app -d fullstack_e2e -c "SELECT current_database(), current_user;"
```

The app's Hibernate configuration creates/updates the table when the backend starts. Verify saved data directly:

```powershell
docker exec -it learning-postgres psql -h localhost -U expenses_app -d fullstack_e2e -c "SELECT id, expense_date, category, description, amount FROM expenses ORDER BY expense_date DESC, id DESC;"
```

## Run locally

Open two PowerShell terminals at the project root (`C:\development\fullstack_e2e`).

Backend (requires the database environment variables above):

```powershell
Get-Content .\.env.example | ForEach-Object {
    if ($_ -match '^\s*([A-Za-z_][A-Za-z0-9_]*)=(.*)$') {
        Set-Item -Path "Env:$($matches[1])" -Value $matches[2]
    }
}
Set-Location .\backend
mvn spring-boot:run
```

Frontend:

```powershell
cd frontend
npm install
npm run dev
```

Open the Vite URL printed in the terminal (normally `http://localhost:5173`). To change the frontend API URL, copy `frontend\.env.example` to `frontend\.env.local`, update `VITE_API_BASE_URL`, and restart Vite.

## Run in Docker

For the two-container local Docker setup (frontend + backend, reusing the existing PostgreSQL container), see [DOCKER-NOTES.md](./DOCKER-NOTES.md).

## REST API

All endpoints are under `http://localhost:8080/api/expenses`.

| Method | Path | Purpose |
|---|---|---|
| GET | `/expenses` | List records ordered by date descending, then ID descending |
| GET | `/expenses?search=text` | Case-insensitive description/category search |
| GET | `/expenses/{id}` | Retrieve one record |
| POST | `/expenses` | Create (returns 201) |
| PUT | `/expenses/{id}` | Update |
| DELETE | `/expenses/{id}` | Delete (returns 204) |

JSON fields are `expenseDate` (`YYYY-MM-DD`), `category`, `description`, and `amount`. Supported categories are Groceries, Utilities, Transportation, Dining, Shopping, Healthcare, and Other. Validation errors return HTTP 400 with a `fieldErrors` object; a missing ID returns HTTP 404. Amounts are stored as decimal values, not floating-point database columns.

## Tests and build

Backend tests use an isolated in-memory H2 database and do not connect to PostgreSQL or alter `notesdb`:

```powershell
cd backend
mvn test
```

Build the frontend:

```powershell
cd frontend
npm install
npm run build
```

## Manual end-to-end check

With the database, backend, and frontend running, use the app to create an expense, search by a word or category, edit it, and delete it (confirm when prompted). Create another record and restart the backend to verify it remains; the `psql` query above independently checks persisted rows.
