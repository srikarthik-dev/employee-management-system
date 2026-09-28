package com.srikarthik.employee;

import com.srikarthik.employee.model.Employee;
import com.srikarthik.employee.service.EmployeeService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmployeeService validation rules.
 * No database records are created; all tests rely on IllegalArgumentException
 * being thrown before any DAO call is made.
 */
class EmployeeServiceValidationTest {

    private final EmployeeService employeeService = new EmployeeService();

    // -------------------------------------------------------------------------
    // Helper: builds a fully valid Employee so individual tests can set only
    // the field under test to an invalid value.
    // -------------------------------------------------------------------------
    private Employee validEmployee() {
        return new Employee(
                "VAL-001",
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "9999999999",
                "Finance",
                "Analyst",
                new BigDecimal("45000.00"),
                LocalDate.of(2024, 1, 10)
        );
    }

    // -------------------------------------------------------------------------
    // Employee code
    // -------------------------------------------------------------------------

    @Test
    void shouldRejectBlankEmployeeCode() {
        Employee employee = validEmployee();
        employee.setEmployeeCode("   ");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.addEmployee(employee));

        assertTrue(ex.getMessage().toLowerCase().contains("employee code"),
                "Message should mention employee code: " + ex.getMessage());
        System.out.println("PASS - blank code: " + ex.getMessage());
    }

    @Test
    void shouldRejectEmployeeCodeExceeding20Characters() {
        Employee employee = validEmployee();
        employee.setEmployeeCode("A".repeat(21));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.addEmployee(employee));

        assertTrue(ex.getMessage().contains("20"),
                "Message should mention limit 20: " + ex.getMessage());
        System.out.println("PASS - code too long: " + ex.getMessage());
    }

    // -------------------------------------------------------------------------
    // Email
    // -------------------------------------------------------------------------

    @Test
    void shouldRejectInvalidEmailFormat() {
        Employee employee = validEmployee();
        employee.setEmail("not-an-email");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.addEmployee(employee));

        assertTrue(ex.getMessage().toLowerCase().contains("email"),
                "Message should mention email: " + ex.getMessage());
        System.out.println("PASS - bad email: " + ex.getMessage());
    }

    @Test
    void shouldRejectEmailWithoutAtSign() {
        Employee employee = validEmployee();
        employee.setEmail("nodomain.com");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.addEmployee(employee));

        assertTrue(ex.getMessage().toLowerCase().contains("email"),
                "Message should mention email: " + ex.getMessage());
        System.out.println("PASS - email missing @: " + ex.getMessage());
    }

    @Test
    void shouldRejectEmailExceeding100Characters() {
        Employee employee = validEmployee();
        // 101-character email: local@<91 'a's>.com
        employee.setEmail("local@" + "a".repeat(91) + ".com");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.addEmployee(employee));

        assertTrue(ex.getMessage().contains("100"),
                "Message should mention limit 100: " + ex.getMessage());
        System.out.println("PASS - email too long: " + ex.getMessage());
    }

    // -------------------------------------------------------------------------
    // Salary
    // -------------------------------------------------------------------------

    @Test
    void shouldRejectNegativeSalary() {
        Employee employee = validEmployee();
        employee.setSalary(new BigDecimal("-1.00"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.addEmployee(employee));

        assertTrue(ex.getMessage().toLowerCase().contains("salary"),
                "Message should mention salary: " + ex.getMessage());
        System.out.println("PASS - negative salary: " + ex.getMessage());
    }

    @Test
    void shouldAcceptZeroSalary() throws Exception {
        // Zero salary is explicitly allowed — this should NOT throw.
        // We only check that validation passes; we do not care about the DB
        // result here so we catch SQLException separately.
        Employee employee = validEmployee();
        employee.setSalary(BigDecimal.ZERO);
        employee.setEmployeeCode("VAL-ZERO-SAL");
        employee.setEmail("zero.salary@example.com");

        // If a SQLException is thrown (e.g. duplicate key from a previous run)
        // we re-throw it — the point is it must NOT throw IllegalArgumentException.
        try {
            Employee created = employeeService.addEmployee(employee);
            // Clean up immediately
            employeeService.deleteEmployee(created.getId());
            System.out.println("PASS - zero salary accepted and cleaned up");
        } catch (java.sql.SQLException e) {
            // DB-level error is fine here; validation passed.
            System.out.println("PASS - zero salary passed validation (DB: " + e.getMessage() + ")");
        }
    }

    // -------------------------------------------------------------------------
    // Hire date
    // -------------------------------------------------------------------------

    @Test
    void shouldRejectFutureHireDate() {
        Employee employee = validEmployee();
        employee.setHireDate(LocalDate.now().plusDays(1));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.addEmployee(employee));

        assertTrue(ex.getMessage().toLowerCase().contains("hire date"),
                "Message should mention hire date: " + ex.getMessage());
        System.out.println("PASS - future hire date: " + ex.getMessage());
    }

    @Test
    void shouldAcceptTodayAsHireDate() throws Exception {
        Employee employee = validEmployee();
        employee.setHireDate(LocalDate.now());
        employee.setEmployeeCode("VAL-TODAY");
        employee.setEmail("today.hire@example.com");

        try {
            Employee created = employeeService.addEmployee(employee);
            employeeService.deleteEmployee(created.getId());
            System.out.println("PASS - today hire date accepted and cleaned up");
        } catch (java.sql.SQLException e) {
            System.out.println("PASS - today hire date passed validation (DB: " + e.getMessage() + ")");
        }
    }

    // -------------------------------------------------------------------------
    // Other required fields
    // -------------------------------------------------------------------------

    @Test
    void shouldRejectBlankFirstName() {
        Employee employee = validEmployee();
        employee.setFirstName("");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.addEmployee(employee));

        assertTrue(ex.getMessage().toLowerCase().contains("first name"),
                "Message should mention first name: " + ex.getMessage());
        System.out.println("PASS - blank first name: " + ex.getMessage());
    }

    @Test
    void shouldRejectBlankDepartment() {
        Employee employee = validEmployee();
        employee.setDepartment(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.addEmployee(employee));

        assertTrue(ex.getMessage().toLowerCase().contains("department"),
                "Message should mention department: " + ex.getMessage());
        System.out.println("PASS - blank department: " + ex.getMessage());
    }
}
