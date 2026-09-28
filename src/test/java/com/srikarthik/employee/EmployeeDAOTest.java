package com.srikarthik.employee;

import com.srikarthik.employee.dao.EmployeeDAO;
import com.srikarthik.employee.model.Employee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDAOTest {

    @Test
    void shouldPerformEmployeeCRUD() throws Exception {

        EmployeeDAO employeeDAO = new EmployeeDAO();

        // CREATE
        Employee employee = new Employee(
                "EMP001",
                "Srikarthik",
                "K",
                "srikarthik.test@example.com",
                "9876543210",
                "IT",
                "Software Developer",
                new BigDecimal("50000.00"),
                LocalDate.of(2026, 1, 15)
        );

        Employee created = employeeDAO.create(employee);

        assertTrue(created.getId() > 0);
        System.out.println("CREATE successful. ID: " + created.getId());

        int employeeId = created.getId();

        // READ
        Employee found = employeeDAO.findById(employeeId);

        assertNotNull(found);
        assertEquals("EMP001", found.getEmployeeCode());
        assertEquals("Srikarthik", found.getFirstName());

        System.out.println("READ successful: " + found);

        // UPDATE
        found.setDesignation("Senior Software Developer");
        found.setSalary(new BigDecimal("60000.00"));

        boolean updated = employeeDAO.update(found);

        assertTrue(updated);

        Employee updatedEmployee = employeeDAO.findById(employeeId);

        assertNotNull(updatedEmployee);
        assertEquals("Senior Software Developer",
                updatedEmployee.getDesignation());
        assertEquals(new BigDecimal("60000.00"),
                updatedEmployee.getSalary());

        System.out.println("UPDATE successful.");

        // DELETE
        boolean deleted = employeeDAO.delete(employeeId);

        assertTrue(deleted);

        Employee deletedEmployee = employeeDAO.findById(employeeId);

        assertNull(deletedEmployee);

        System.out.println("DELETE successful.");
    }

    @Test
    void shouldSearchEmployees() throws Exception {

        EmployeeDAO employeeDAO = new EmployeeDAO();

        // Create two employees with distinct attributes
        Employee emp1 = new Employee(
                "SRCH-EMP-001",
                "Alice",
                "Wonderland",
                "alice.srch@example.com",
                "1111111111",
                "Engineering",
                "Engineer",
                new java.math.BigDecimal("55000.00"),
                java.time.LocalDate.of(2025, 3, 1)
        );

        Employee emp2 = new Employee(
                "SRCH-EMP-002",
                "Bob",
                "Builder",
                "bob.srch@example.com",
                "2222222222",
                "Marketing",
                "Manager",
                new java.math.BigDecimal("65000.00"),
                java.time.LocalDate.of(2025, 6, 1)
        );

        Employee created1 = employeeDAO.create(emp1);
        Employee created2 = employeeDAO.create(emp2);

        assertTrue(created1.getId() > 0);
        assertTrue(created2.getId() > 0);

        try {
            // Search by employee code
            java.util.List<Employee> byCode =
                    employeeDAO.search("SRCH-EMP-001");

            assertFalse(byCode.isEmpty(), "Should find at least one result by code");
            assertTrue(byCode.stream().anyMatch(
                    e -> "SRCH-EMP-001".equals(e.getEmployeeCode())),
                    "emp1 should appear in code search");

            System.out.println("SEARCH by code: " + byCode.size() + " result(s)");

            // Search by first name
            java.util.List<Employee> byName =
                    employeeDAO.search("Alice");

            assertFalse(byName.isEmpty(), "Should find at least one result by first name");
            assertTrue(byName.stream().anyMatch(
                    e -> "Alice".equals(e.getFirstName())),
                    "emp1 should appear in name search");

            System.out.println("SEARCH by name: " + byName.size() + " result(s)");

            // Search by department — only emp2 should be in Marketing
            java.util.List<Employee> byDept =
                    employeeDAO.search("Marketing");

            assertFalse(byDept.isEmpty(), "Should find at least one result by department");
            assertTrue(byDept.stream().anyMatch(
                    e -> "Marketing".equals(e.getDepartment())),
                    "emp2 should appear in department search");

            System.out.println("SEARCH by department: " + byDept.size() + " result(s)");

        } finally {
            // Clean up both employees regardless of assertion outcome
            employeeDAO.delete(created1.getId());
            employeeDAO.delete(created2.getId());

            System.out.println("SEARCH test cleanup complete.");
        }
    }
}
