package com.srikarthik.employee;

import com.srikarthik.employee.model.Employee;
import com.srikarthik.employee.service.EmployeeService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final EmployeeService employeeService = new EmployeeService();

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("    EMPLOYEE MANAGEMENT SYSTEM");
        System.out.println("=================================");

        boolean running = true;

        while (running) {
            displayMenu();

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> addEmployee();
                    case "2" -> viewEmployee();
                    case "3" -> viewAllEmployees();
                    case "4" -> updateEmployee();
                    case "5" -> deleteEmployee();
                    case "6" -> {
                        running = false;
                        System.out.println("\nThank you for using Employee Management System.");
                    }
                    default -> System.out.println("\nInvalid choice. Please select 1-6.");
                }
            } catch (SQLException e) {
                System.out.println("\nDatabase error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("\nValidation error: " + e.getMessage());
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n---------------------------------");
        System.out.println("1. Add Employee");
        System.out.println("2. View Employee");
        System.out.println("3. View All Employees");
        System.out.println("4. Update Employee");
        System.out.println("5. Delete Employee");
        System.out.println("6. Exit");
        System.out.println("---------------------------------");
        System.out.print("Enter choice: ");
    }

    private static void addEmployee() throws SQLException {

        System.out.println("\n--- Add Employee ---");

        String employeeCode = readRequired("Employee Code: ");
        String firstName = readRequired("First Name: ");
        String lastName = readRequired("Last Name: ");
        String email = readRequired("Email: ");
        String phone = readOptional("Phone: ");
        String department = readRequired("Department: ");
        String designation = readRequired("Designation: ");

        BigDecimal salary = readSalary();
        LocalDate hireDate = readDate("Hire Date (YYYY-MM-DD): ");

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

        System.out.println("\nEmployee added successfully!");
        System.out.println("Generated ID: " + created.getId());
    }

    private static void viewEmployee() throws SQLException {

        System.out.println("\n--- View Employee ---");

        int id = readId("Employee ID: ");

        Employee employee = employeeService.getEmployee(id);

        if (employee == null) {
            System.out.println("\nEmployee not found.");
            return;
        }

        printEmployee(employee);
    }

    private static void viewAllEmployees() throws SQLException {

        System.out.println("\n--- All Employees ---");

        List<Employee> employees = employeeService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        for (Employee employee : employees) {
            printEmployee(employee);
            System.out.println("---------------------------------");
        }

        System.out.println("Total employees: " + employees.size());
    }

    private static void updateEmployee() throws SQLException {

        System.out.println("\n--- Update Employee ---");

        int id = readId("Employee ID: ");

        Employee employee = employeeService.getEmployee(id);

        if (employee == null) {
            System.out.println("\nEmployee not found.");
            return;
        }

        System.out.println("\nCurrent employee:");
        printEmployee(employee);

        System.out.println("\nEnter new values:");

        employee.setEmployeeCode(readRequired("Employee Code: "));
        employee.setFirstName(readRequired("First Name: "));
        employee.setLastName(readRequired("Last Name: "));
        employee.setEmail(readRequired("Email: "));
        employee.setPhone(readOptional("Phone: "));
        employee.setDepartment(readRequired("Department: "));
        employee.setDesignation(readRequired("Designation: "));
        employee.setSalary(readSalary());
        employee.setHireDate(readDate("Hire Date (YYYY-MM-DD): "));

        boolean updated = employeeService.updateEmployee(employee);

        if (updated) {
            System.out.println("\nEmployee updated successfully!");
        } else {
            System.out.println("\nEmployee could not be updated.");
        }
    }

    private static void deleteEmployee() throws SQLException {

        System.out.println("\n--- Delete Employee ---");

        int id = readId("Employee ID: ");

        Employee employee = employeeService.getEmployee(id);

        if (employee == null) {
            System.out.println("\nEmployee not found.");
            return;
        }

        System.out.println("\nEmployee to delete:");
        printEmployee(employee);

        System.out.print("\nAre you sure? (yes/no): ");
        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Delete cancelled.");
            return;
        }

        boolean deleted = employeeService.deleteEmployee(id);

        if (deleted) {
            System.out.println("\nEmployee deleted successfully!");
        } else {
            System.out.println("\nEmployee could not be deleted.");
        }
    }

    private static void printEmployee(Employee employee) {

        System.out.println("\nEmployee ID   : " + employee.getId());
        System.out.println("Employee Code : " + employee.getEmployeeCode());
        System.out.println("Name          : " +
                employee.getFirstName() + " " + employee.getLastName());
        System.out.println("Email         : " + employee.getEmail());
        System.out.println("Phone         : " + employee.getPhone());
        System.out.println("Department    : " + employee.getDepartment());
        System.out.println("Designation   : " + employee.getDesignation());
        System.out.println("Salary        : " + employee.getSalary());
        System.out.println("Hire Date     : " + employee.getHireDate());
    }

    private static String readRequired(String prompt) {

        while (true) {
            System.out.print(prompt);

            String value = scanner.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field is required.");
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

                System.out.println("ID must be greater than zero.");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static BigDecimal readSalary() {

        while (true) {

            System.out.print("Salary: ");

            try {
                BigDecimal salary =
                        new BigDecimal(scanner.nextLine().trim());

                if (salary.signum() >= 0) {
                    return salary;
                }

                System.out.println("Salary cannot be negative.");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid salary.");
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
                        "Invalid date. Use YYYY-MM-DD."
                );
            }
        }
    }
}
