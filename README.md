# Employee Management System

A command-line Java application for managing employee records using MySQL and JDBC.
Built with a clean DAO + Service + Model architecture, this project demonstrates
core Java skills, relational database integration, layered application design,
and professional software engineering practices.

---

## Features

| Feature | Description |
|---|---|
| **Add Employee** | Create a new employee record with full details |
| **View Employee** | Look up a single employee by ID |
| **View All Employees** | List every employee in the database |
| **Search Employee** | Partial-match search across code, name, email, and department |
| **Update Employee** | Modify any field of an existing employee record |
| **Delete Employee** | Remove an employee record with a confirmation prompt |
| **Input Validation** | Required fields, length limits, email format, salary range, hire date |
| **Duplicate Handling** | Friendly error when employee code or email already exists |
| **MySQL Persistence** | All data stored in a MySQL database via JDBC |
| **JUnit Testing** | Automated tests covering DB connectivity, CRUD, search, and validation |

---

## Technologies Used

| Technology | Version / Detail |
|---|---|
| Java | 17 |
| Maven | Build tool and dependency management |
| MySQL | Relational database |
| MySQL Connector/J | 9.4.0 — JDBC driver |
| JUnit Jupiter | 5.13.4 — unit and integration tests |
| Git & GitHub | Version control |

---

## Project Architecture

```
src/main/java/com/srikarthik/employee/
├── Main.java                    ← Command-line interface and application entry point
├── config/
│   └── DatabaseConnection.java  ← JDBC connection management
├── dao/
│   └── EmployeeDAO.java         ← All database operations (CRUD + Search)
├── model/
│   └── Employee.java            ← Employee data model / POJO
└── service/
    └── EmployeeService.java     ← Business rules, input validation, DAO delegation
```

### Layer Responsibilities

| Layer | Class | Responsibility |
|---|---|---|
| **Model** | `Employee.java` | Represents employee data with getters and setters |
| **DAO** | `EmployeeDAO.java` | Executes parameterised SQL queries against MySQL |
| **Service** | `EmployeeService.java` | Validates inputs and coordinates DAO calls |
| **Config** | `DatabaseConnection.java` | Loads credentials from `application.properties` and returns a `Connection` |
| **CLI** | `Main.java` | Reads user input, calls the service layer, and displays formatted output |

---

## Database

- **Database name:** `employee_management`
- **Table:** `employees`

| Column | Type | Constraints |
|---|---|---|
| `id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` |
| `employee_code` | `VARCHAR(20)` | `NOT NULL`, `UNIQUE` |
| `first_name` | `VARCHAR(50)` | `NOT NULL` |
| `last_name` | `VARCHAR(50)` | `NOT NULL` |
| `email` | `VARCHAR(100)` | `NOT NULL`, `UNIQUE` |
| `phone` | `VARCHAR(20)` | Optional |
| `department` | `VARCHAR(50)` | `NOT NULL` |
| `designation` | `VARCHAR(100)` | `NOT NULL` |
| `salary` | `DECIMAL(10,2)` | `NOT NULL` |
| `hire_date` | `DATE` | `NOT NULL` |
| `created_at` | `TIMESTAMP` | Auto-set on insert |
| `updated_at` | `TIMESTAMP` | Auto-updated on change |

The full DDL script is located at [`database/schema.sql`](database/schema.sql).

---

## Setup Instructions

### Prerequisites

- JDK 17 or later
- Apache Maven 3.6+
- MySQL 8.0+

---

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd employee-management-system
```

---

### 2. Create the Database

Log in to MySQL and run the schema script:

```bash
mysql -u root -p < database/schema.sql
```

Or run the script manually inside the MySQL shell:

```sql
source /path/to/employee-management-system/database/schema.sql;
```

This creates the `employee_management` database and the `employees` table if they do not already exist.

---

### 3. Configure the Database Connection

Create the following file (it is listed in `.gitignore` and will **not** be committed):

```
src/main/resources/application.properties
```

Add your connection details:

```properties
db.url=jdbc:mysql://localhost:3306/employee_management
db.username=root
db.password=YOUR_MYSQL_PASSWORD
```

> **Security:** Never commit `application.properties` to version control.
> It is already excluded via `.gitignore`.

---

### 4. Build

```bash
mvn clean compile
```

Expected output: `BUILD SUCCESS`

---

### 5. Run Tests

```bash
mvn clean test
```

Expected output:

```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

### 6. Run the Application

This project does not include an exec plugin in `pom.xml`.
The recommended way to run the application is directly from your IDE
(IntelliJ IDEA, Eclipse, VS Code with Java extensions) by running `Main.java`.

Alternatively, build a JAR with dependencies and run it from the terminal:

```bash
# Package with dependencies
mvn package

# Run (adjust the JAR filename if the version changes)
java -cp target/employee-management-system-1.0.0.jar com.srikarthik.employee.Main
```

> If the JAR does not bundle dependencies, add them to the classpath explicitly,
> or run via your IDE which handles this automatically.

---

## Application Menu

```
========================================
       EMPLOYEE MANAGEMENT SYSTEM
========================================

  1.  Add Employee
  2.  View Employee
  3.  View All Employees
  4.  Search Employee
  5.  Update Employee
  6.  Delete Employee
  7.  Exit

========================================
  Enter choice:
```

---

## Validation Rules

All validation is enforced in the **service layer** (`EmployeeService.java`) before any database call is made.

| Field | Rules |
|---|---|
| Employee Code | Required · Max 20 characters |
| First Name | Required · Max 50 characters |
| Last Name | Required · Max 50 characters |
| Email | Required · Max 100 characters · Must contain `@` and a `.` after `@` |
| Phone | Optional · Max 20 characters |
| Department | Required · Max 50 characters |
| Designation | Required · Max 100 characters |
| Salary | Required · Must be zero or greater |
| Hire Date | Required · Must not be in the future · Format: `YYYY-MM-DD` |
| Employee ID | Must be a positive integer |

**Duplicate handling:** If an employee code or email already exists in the database, the application displays a clear message instead of a raw SQL error:

```
Employee code or email already exists.
```

---

## Testing

Tests are located in:

```
src/test/java/com/srikarthik/employee/
├── DatabaseConnectionTest.java          ← Verifies MySQL connectivity
├── EmployeeDAOTest.java                 ← Tests CRUD operations and search
└── EmployeeServiceValidationTest.java   ← Tests all service-layer validation rules
```

| Test Class | What it covers |
|---|---|
| `DatabaseConnectionTest` | Establishes a live database connection |
| `EmployeeDAOTest` | Full employee lifecycle: create, read, update, delete, search |
| `EmployeeServiceValidationTest` | Blank fields, length limits, invalid email, negative salary, future hire date |

**Verified result:**

```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## Project Structure

```
employee-management-system/
├── database/
│   └── schema.sql                    ← DDL to create database and table
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/srikarthik/employee/
│   │   │       ├── Main.java
│   │   │       ├── config/
│   │   │       │   └── DatabaseConnection.java
│   │   │       ├── dao/
│   │   │       │   └── EmployeeDAO.java
│   │   │       ├── model/
│   │   │       │   └── Employee.java
│   │   │       └── service/
│   │   │           └── EmployeeService.java
│   │   └── resources/
│   │       └── application.properties  ← Not committed (see .gitignore)
│   └── test/
│       └── java/
│           └── com/srikarthik/employee/
│               ├── DatabaseConnectionTest.java
│               ├── EmployeeDAOTest.java
│               └── EmployeeServiceValidationTest.java
├── .gitignore
├── pom.xml
└── README.md
```

---

## Security Note

Database credentials are stored locally in `src/main/resources/application.properties`
and are **never committed to Git**. This file is explicitly listed in `.gitignore`.

Do not hardcode credentials anywhere in the Java source files.

---

## Future Enhancements

The following improvements are planned as future work and are **not currently implemented**:

- **Web interface** — a browser-based frontend (e.g. using JSP, Thymeleaf, or React)
- **REST API** — HTTP endpoints using Spring Boot or Jakarta EE
- **Authentication** — login system with role-based access control
- **Pagination** — efficient handling of large employee datasets
- **Advanced filtering** — multi-field filter and sort options
- **Reporting / Export** — CSV or PDF export of employee records
- **Deployment** — cloud hosting with Docker and CI/CD pipeline

---

## Author

**Srikarthik K**  
Java Developer  
[GitHub](https://github.com/srikarthikk)

---

*Built as a college project demonstrating Java, MySQL, JDBC, Maven, and JUnit.*
