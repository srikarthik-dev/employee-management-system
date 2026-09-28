package com.srikarthik.employee;

import com.srikarthik.employee.model.Employee;
import com.srikarthik.employee.service.EmployeeService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final String DIVIDER  = "----------------------------------------";
    private static final String DIVIDER_THICK = "========================================";

    private static final Scanner scanner = new Scanner(System.in);
    private static final EmployeeService employeeService = new EmployeeService();

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    public static void main(String[] args) {

        System.out.println();
        System.out.println(DIVIDER_THICK);
        System.out.println("       EMPLOYEE MANAGEMENT SYSTEM");
        System.out.println(DIVIDER_THICK);

        boolean running = true;

        while (running) {
            displayMenu();

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> addEmployee();
                    case "2" -> viewEmployee();
                    case "3" -> viewAllEmployees();
                    case "4" -> searchEmployees();
                    case "5" -> updateEmployee();
                    case "6" -> deleteEmployee();
                    case "7" -> {
                        running = false;
                        printExit();
                    }
                    default -> System.out.println("\n  Invalid choice. Please select 1-7.");
                }
            } catch (SQLException e) {
                // MySQL error code 1062 = duplicate entry (UNIQUE constraint)
                if ("23000".equals(e.getSQLState()) || e.getErrorCode() == 1062) {
                    System.out.println(
                            "\n  Employee code or email already exists.");
                } else {
                    System.out.println("\n  Database error: " + e.getMessage());
                }
            } catch (IllegalArgumentException e) {
                System.out.println("\n  Validation error: " + e.getMessage());
            }

            if (running) {
                System.out.println();
                System.out.print("  Press Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    // -------------------------------------------------------------------------
    // Menu
    // -------------------------------------------------------------------------

    private static void displayMenu() {
        System.out.println();
        System.out.println(DIVIDER_THICK);
        System.out.println("  1.  Add Employee");
        System.out.println("  2.  View Employee");
        System.out.println("  3.  View All Employees");
        System.out.println("  4.  Search Employee");
        System.out.println("  5.  Update Employee");
        System.out.println("  6.  Delete Employee");
        System.out.println("  7.  Exit");
        System.out.println(DIVIDER_THICK);
        System.out.print("  Enter choice: ");
    }

    // -------------------------------------------------------------------------
    // Add Employee
    // -------------------------------------------------------------------------

    private static void addEmployee() throws SQLException {

        printSectionHeader("Add Employee");

        String employeeCode = readRequired("  Employee Code        : ");
        String firstName    = readRequired("  First Name           : ");
        String lastName     = readRequired("  Last Name            : ");
        String email        = readRequired("  Email                : ");
        String phone        = readOptional("  Phone (optional)     : ");
        String department   = readRequired("  Department           : ");
        String designation  = readRequired("  Designation          : ");
        BigDecimal salary   = readSalary();
        LocalDate hireDate  = readDate("  Hire Date (YYYY-MM-DD): ");

        Employee employee = new Employee(
                employeeCode,
                firstName,
                lastName,
                email,
                phone,
                department,
                designation,
                salary,
                hireDate
        );

        Employee created = employeeService.addEmployee(employee);

        System.out.println();
        System.out.println(DIVIDER);
        System.out.println("  Employee added successfully!");
        System.out.println("  Generated ID : " + created.getId());
        System.out.println(DIVIDER);
    }

    // -------------------------------------------------------------------------
    // View Employee
    // -------------------------------------------------------------------------

    private static void viewEmployee() throws SQLException {

        printSectionHeader("View Employee");

        int id = readId("  Employee ID: ");

        Employee employee = employeeService.getEmployee(id);

        if (employee == null) {
            System.out.println("\n  Employee not found.");
            return;
        }

        printEmployee(employee);
    }

    // -------------------------------------------------------------------------
    // View All Employees
    // -------------------------------------------------------------------------

    private static void viewAllEmployees() throws SQLException {

        printSectionHeader("All Employees");

        List<Employee> employees = employeeService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("\n  No employees found.");
            return;
        }

        for (Employee employee : employees) {
            printEmployee(employee);
        }

        System.out.println();
        System.out.println("  Total employees: " + employees.size());
        System.out.println(DIVIDER);
    }

    // -------------------------------------------------------------------------
    // Search Employees
    // -------------------------------------------------------------------------

    private static void searchEmployees() throws SQLException {

        printSectionHeader("Search Employees");

        String keyword = readRequired("  Search keyword: ");

        List<Employee> employees = employeeService.searchEmployees(keyword);

        if (employees.isEmpty()) {
            System.out.println("\n  No employees found for: \"" + keyword + "\"");
            return;
        }

        System.out.println();
        System.out.println("  Results for: \"" + keyword + "\"");

        for (Employee employee : employees) {
            printEmployee(employee);
        }

        System.out.println();
        System.out.println("  Total results: " + employees.size());
        System.out.println(DIVIDER);
    }

    // -------------------------------------------------------------------------
    // Update Employee
    // -------------------------------------------------------------------------

    private static void updateEmployee() throws SQLException {

        printSectionHeader("Update Employee");

        int id = readId("  Employee ID: ");

        Employee employee = employeeService.getEmployee(id);

        if (employee == null) {
            System.out.println("\n  Employee not found.");
            return;
        }

        System.out.println("\n  Current record:");
        printEmployee(employee);

        System.out.println();
        System.out.println("  Enter new values:");
        System.out.println(DIVIDER);

        employee.setEmployeeCode(readRequired("  Employee Code        : "));
        employee.setFirstName(readRequired("  First Name           : "));
        employee.setLastName(readRequired("  Last Name            : "));
        employee.setEmail(readRequired("  Email                : "));
        employee.setPhone(readOptional("  Phone (optional)     : "));
        employee.setDepartment(readRequired("  Department           : "));
        employee.setDesignation(readRequired("  Designation          : "));
        employee.setSalary(readSalary());
        employee.setHireDate(readDate("  Hire Date (YYYY-MM-DD): "));

        boolean updated = employeeService.updateEmployee(employee);

        System.out.println();
        System.out.println(DIVIDER);
        if (updated) {
            System.out.println("  Employee updated successfully!");
        } else {
            System.out.println("  Employee could not be updated.");
        }
        System.out.println(DIVIDER);
    }

    // -------------------------------------------------------------------------
    // Delete Employee
    // -------------------------------------------------------------------------

    private static void deleteEmployee() throws SQLException {

        printSectionHeader("Delete Employee");

        int id = readId("  Employee ID: ");

        Employee employee = employeeService.getEmployee(id);

        if (employee == null) {
            System.out.println("\n  Employee not found.");
            return;
        }

        System.out.println("\n  Employee to delete:");
        printEmployee(employee);

        System.out.println();
        System.out.print("  Are you sure you want to delete this record? (yes/no): ");
        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("\n  Delete cancelled.");
            return;
        }

        boolean deleted = employeeService.deleteEmployee(id);

        System.out.println();
        System.out.println(DIVIDER);
        if (deleted) {
            System.out.println("  Employee deleted successfully!");
        } else {
            System.out.println("  Employee could not be deleted.");
        }
        System.out.println(DIVIDER);
    }

    // -------------------------------------------------------------------------
    // Display helpers
    // -------------------------------------------------------------------------

    private static void printSectionHeader(String title) {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.println("  " + title);
        System.out.println(DIVIDER);
    }

    private static void printEmployee(Employee employee) {
        System.out.println(DIVIDER);
        System.out.println("  Employee ID   : " + employee.getId());
        System.out.println("  Employee Code : " + employee.getEmployeeCode());
        System.out.println("  Name          : " +
                employee.getFirstName() + " " + employee.getLastName());
        System.out.println("  Email         : " + employee.getEmail());
        System.out.println("  Phone         : " +
                (employee.getPhone() != null ? employee.getPhone() : "-"));
        System.out.println("  Department    : " + employee.getDepartment());
        System.out.println("  Designation   : " + employee.getDesignation());
        System.out.println("  Salary        : " + employee.getSalary());
        System.out.println("  Hire Date     : " + employee.getHireDate());
        System.out.println(DIVIDER);
    }

    private static void printExit() {
        System.out.println();
        System.out.println(DIVIDER_THICK);
        System.out.println("  Thank you for using Employee Management System.");
        System.out.println("  Goodbye!");
        System.out.println(DIVIDER_THICK);
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Input helpers
    // -------------------------------------------------------------------------

    private static String readRequired(String prompt) {

        while (true) {
            System.out.print(prompt);

            String value = scanner.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("  This field is required.");
        }
    }

    private static String readOptional(String prompt) {

        System.out.print(prompt);

        String value = scanner.nextLine().trim();

        return value.isEmpty() ? null : value;
    }

    private static int readId(String prompt) {

        while (true) {

            System.out.print(prompt);

            try {
                int id = Integer.parseInt(scanner.nextLine().trim());

                if (id > 0) {
                    return id;
                }

                System.out.println("  ID must be greater than zero.");

            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid number.");
            }
        }
    }

    private static BigDecimal readSalary() {

        while (true) {

            System.out.print("  Salary                : ");

            try {
                BigDecimal salary =
                        new BigDecimal(scanner.nextLine().trim());

                if (salary.signum() >= 0) {
                    return salary;
                }

                System.out.println("  Salary cannot be negative.");

            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid salary.");
            }
        }
    }

    private static LocalDate readDate(String prompt) {

        while (true) {

            System.out.print(prompt);

            try {
                return LocalDate.parse(scanner.nextLine().trim());

            } catch (Exception e) {
                System.out.println(
                        "  Invalid date. Use YYYY-MM-DD format.");
            }
        }
    }
}
