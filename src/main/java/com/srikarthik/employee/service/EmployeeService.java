package com.srikarthik.employee.service;

import com.srikarthik.employee.dao.EmployeeDAO;
import com.srikarthik.employee.model.Employee;

import java.sql.SQLException;
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

    private void validateEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }

        if (isBlank(employee.getEmployeeCode())) {
            throw new IllegalArgumentException("Employee code is required");
        }

        if (isBlank(employee.getFirstName())) {
            throw new IllegalArgumentException("First name is required");
        }

        if (isBlank(employee.getLastName())) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (isBlank(employee.getEmail())) {
            throw new IllegalArgumentException("Email is required");
        }

        if (isBlank(employee.getDepartment())) {
            throw new IllegalArgumentException("Department is required");
        }

        if (isBlank(employee.getDesignation())) {
            throw new IllegalArgumentException("Designation is required");
        }

        if (employee.getSalary() == null ||
                employee.getSalary().signum() < 0) {
            throw new IllegalArgumentException("Salary must be zero or greater");
        }

        if (employee.getHireDate() == null) {
            throw new IllegalArgumentException("Hire date is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
