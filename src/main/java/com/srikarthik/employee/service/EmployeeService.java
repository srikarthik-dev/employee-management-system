package com.srikarthik.employee.service;

import com.srikarthik.employee.dao.EmployeeDAO;
import com.srikarthik.employee.model.Employee;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    public Employee addEmployee(Employee employee) throws SQLException {
        validateEmployee(employee);
        return employeeDAO.create(employee);
    }

    public Employee getEmployee(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }

        return employeeDAO.findById(id);
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return employeeDAO.findAll();
    }

    public List<Employee> searchEmployees(String keyword) throws SQLException {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword is required");
        }

        return employeeDAO.search(keyword.trim());
    }

    public boolean updateEmployee(Employee employee) throws SQLException {
        validateEmployee(employee);

        if (employee.getId() <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }

        return employeeDAO.update(employee);
    }

    public boolean deleteEmployee(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }

        return employeeDAO.delete(id);
    }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    private void validateEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }

        // Employee code: required, max 20 chars
        if (isBlank(employee.getEmployeeCode())) {
            throw new IllegalArgumentException("Employee code is required");
        }
        if (employee.getEmployeeCode().length() > 20) {
            throw new IllegalArgumentException(
                    "Employee code must not exceed 20 characters");
        }

        // First name: required, max 50 chars
        if (isBlank(employee.getFirstName())) {
            throw new IllegalArgumentException("First name is required");
        }
        if (employee.getFirstName().length() > 50) {
            throw new IllegalArgumentException(
                    "First name must not exceed 50 characters");
        }

        // Last name: required, max 50 chars
        if (isBlank(employee.getLastName())) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (employee.getLastName().length() > 50) {
            throw new IllegalArgumentException(
                    "Last name must not exceed 50 characters");
        }

        // Email: required, max 100 chars, basic format
        if (isBlank(employee.getEmail())) {
            throw new IllegalArgumentException("Email is required");
        }
        if (employee.getEmail().length() > 100) {
            throw new IllegalArgumentException(
                    "Email must not exceed 100 characters");
        }
        if (!isValidEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Email format is invalid");
        }

        // Phone: optional, max 20 chars when provided
        if (employee.getPhone() != null && !employee.getPhone().trim().isEmpty()) {
            if (employee.getPhone().length() > 20) {
                throw new IllegalArgumentException(
                        "Phone must not exceed 20 characters");
            }
        }

        // Department: required, max 50 chars
        if (isBlank(employee.getDepartment())) {
            throw new IllegalArgumentException("Department is required");
        }
        if (employee.getDepartment().length() > 50) {
            throw new IllegalArgumentException(
                    "Department must not exceed 50 characters");
        }

        // Designation: required, max 100 chars
        if (isBlank(employee.getDesignation())) {
            throw new IllegalArgumentException("Designation is required");
        }
        if (employee.getDesignation().length() > 100) {
            throw new IllegalArgumentException(
                    "Designation must not exceed 100 characters");
        }

        // Salary: required, zero or greater
        if (employee.getSalary() == null ||
                employee.getSalary().signum() < 0) {
            throw new IllegalArgumentException("Salary must be zero or greater");
        }

        // Hire date: required, must not be in the future
        if (employee.getHireDate() == null) {
            throw new IllegalArgumentException("Hire date is required");
        }
        if (employee.getHireDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Hire date cannot be in the future");
        }
    }

    /**
     * Basic email validation: must contain '@', and at least one '.' after '@'.
     * Intentionally simple — no external library required.
     */
    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return false;
        }
        String afterAt = email.substring(atIndex + 1);
        return afterAt.contains(".") && !afterAt.startsWith(".");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
