# Employee Management System

A robust command-line Java application for managing employee records. Built with a clean layered architecture, this project demonstrates core Java skills, relational database integration, and professional software engineering practices, including automated testing and Dockerized deployment.

---

## 1. Project Overview

- **Type:** CLI-based Application
- **Language:** Java 17
- **Build Tool:** Maven
- **Database:** MySQL
- **Database Access:** JDBC
- **Architecture:** DAO + Service layered architecture

---

## 2. Key Features

- **Add employee:** Create new employee records with full details.
- **View employee:** Look up a single employee by their ID.
- **View all employees:** List every employee in the database.
- **Search:** Partial-match search across code, name, email, and department.
- **Update:** Modify any field of an existing employee record.
- **Delete:** Remove an employee record with a confirmation prompt.
- **Validation:** Strict input validation (required fields, length limits, email format, salary range, hire date).
- **Duplicate handling:** Friendly error messages when an employee code or email already exists.
- **MySQL persistence:** All data is securely stored in a MySQL database.
- **Automated JUnit testing:** Comprehensive test suite covering validation, CRUD, and database connectivity.
- **Dockerized deployment:** Fully containerized setup for easy execution without local dependencies.

---

## 3. Architecture

The application follows a clean, layered architecture to separate concerns:

**Main (CLI) → Service → DAO → Database**

- **Model:** Represents the employee data structure.
- **DAO (Data Access Object):** Executes SQL queries against the MySQL database.
- **Service:** Enforces business rules and input validation, then delegates to the DAO.
- **Main:** Handles the interactive command-line interface and user input/output.

### Docker Compose Architecture
The Dockerized version runs two isolated containers connected via a dedicated Docker network:
- **Employee App Container:** Runs the Java application (built via a multi-stage Dockerfile).
- **MySQL Container:** Runs the MySQL 8.4 database.

---

## 4. Technology Stack

- **Java:** 17
- **Maven:** Build and dependency management
- **MySQL:** 8.4
- **JDBC Driver:** MySQL Connector/J 9.4.0
- **Testing:** JUnit Jupiter 5.13.4
- **Containerization:** Docker & Docker Compose

---

## 5. Project Structure

```text
employee-management-system/
├── Dockerfile                  ← Multi-stage build for the Java app
├── docker-compose.yml          ← Orchestrates the App and MySQL containers
├── .dockerignore               ← Excludes local files from the Docker build
├── .gitignore                  ← Git ignore rules
├── pom.xml                     ← Maven dependencies and build configuration
├── README.md                   ← Project documentation
├── database/
│   └── schema.sql              ← DDL for database and table creation
└── src/
    ├── main/java/com/srikarthik/employee/
    │   ├── Main.java           ← CLI entry point
    │   ├── config/
    │   │   └── DatabaseConnection.java ← Manages JDBC connection (env vars or properties)
    │   ├── dao/
    │   │   └── EmployeeDAO.java
    │   ├── model/
    │   │   └── Employee.java
    │   └── service/
    │       └── EmployeeService.java
    └── test/java/com/srikarthik/employee/
        ├── DatabaseConnectionTest.java
        ├── EmployeeDAOTest.java
        └── EmployeeServiceValidationTest.java
```

---

## 6. Local Development Setup (Without Docker)

If you prefer to run the application using your local Java and MySQL installations:

### Prerequisites
- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### Setup Steps
1. **Clone the repository:**
   ```bash
   git clone https://github.com/srikarthik-dev/employee-management-system.git
   cd employee-management-system
   ```
2. **Create the Database:**
   Run the `database/schema.sql` script in your local MySQL instance.
3. **Configure Connection:**
   Create `src/main/resources/application.properties` (this file is git-ignored):
   ```properties
   db.url=jdbc:mysql://localhost:3306/employee_management
   db.username=YOUR_MYSQL_USERNAME
   db.password=YOUR_MYSQL_PASSWORD
   ```
4. **Run the Application:**
   Run `Main.java` from your IDE, or package it with Maven:
   ```bash
   mvn clean package
   java -cp target/employee-management-system-1.0.0.jar com.srikarthik.employee.Main
   ```

---

## 7. Docker Setup

The simplest way to run the project — no local Java, Maven, or MySQL installation required.

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (includes Docker Compose)

### Quick Start Workflow
```bash
git clone https://github.com/srikarthik-dev/employee-management-system.git
cd employee-management-system
docker compose up --build
```

### How it Works
1. **Network:** Creates an isolated Docker network (`employee-net`).
2. **MySQL Container:** Starts `employee-mysql` and initializes the database using `database/schema.sql` on the first run.
3. **Volume:** Uses a persistent named volume (`employee_mysql_data`) so your data survives container restarts.
4. **Healthcheck:** The `employee-app` container waits until the MySQL container reports a `healthy` status before starting.
5. **App Container:** The Java application is built into a self-contained JAR using a multi-stage `Dockerfile`. It connects to the database via the internal network (`employee-mysql:3306`).
6. **Port Mapping:** The Docker MySQL instance is exposed to your host machine on port `3307` to avoid conflicting with any local MySQL running on port `3306`.

---

## 8. Docker Commands

- **Start and Build (Interactive):**
  ```bash
  docker compose up --build
  ```
- **Start in Background:**
  ```bash
  docker compose up -d
  ```
- **View Running Containers:**
  ```bash
  docker compose ps
  ```
- **View Logs:**
  ```bash
  docker compose logs -f
  ```
- **Stop Containers (preserves data):**
  ```bash
  docker compose down
  ```
- **Stop and Remove Volumes (Full Reset):**
  ```bash
  docker compose down -v
  ```
- **Rebuild from Scratch (Ignoring Cache):**
  ```bash
  docker compose build --no-cache
  ```

---

## 9. Database Persistence

By default, the MySQL container uses a named Docker volume to store data.
- Running `docker compose down` will stop the containers, but **preserve** your employee records.
- Running `docker compose down -v` will **permanently delete** the database volume, causing the database to be initialized fresh (empty) on the next startup.

---

## 10. Configuration & Security

- **Environment Variables:** When running in Docker, `DatabaseConnection.java` automatically falls back to reading the `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables provided by `docker-compose.yml`.
- **Git Ignore:** The local `application.properties` file is strictly ignored by Git to prevent accidental commits of local database credentials.
- **Docker Credentials:** The credentials found in `docker-compose.yml` are Docker-local development values only and are securely isolated within the Docker network.
- **No Bundled Credentials:** The multi-stage `Dockerfile` explicitly removes any local `application.properties` before packaging the JAR, ensuring host credentials are never bundled into the Docker image.

---

## 11. Testing

The project includes a robust test suite covering database connectivity, DAO operations, and service-layer validation.

To run the automated tests locally:
```bash
mvn clean test
```
The project currently has **14 automated tests**, all of which must pass for a successful build.

---

## 12. Troubleshooting

- **Docker not running:** Ensure Docker Desktop is open and the Docker daemon is running before executing `docker compose` commands.
- **Port conflict:** If port `3307` is already in use on your host machine, modify the `ports` mapping in `docker-compose.yml` (e.g., `"3308:3306"`).
- **Containers not healthy:** Check the MySQL logs using `docker compose logs employee-mysql` to see why the database failed to initialize.
- **Stale database volume:** If you modify `schema.sql`, the database won't automatically update if the volume already exists. Run `docker compose down -v` to reset it.
- **Rebuilding after code changes:** If you change Java code, always use `docker compose up --build` to ensure the application image is recompiled.

---

## 13. Production / Deployment Note

The included Docker Compose setup and `Dockerfile` are intended for local development, demonstration, and portfolio deployment. While they demonstrate solid containerization principles (like multi-stage builds and healthchecks), they use development credentials and configuration, and are not intended for a hardened production environment without further security adjustments.

---

*Built by Srikarthik K as a comprehensive project demonstrating Java, MySQL, JDBC, Maven, JUnit, and Docker.*
