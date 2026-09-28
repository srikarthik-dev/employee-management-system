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
}
