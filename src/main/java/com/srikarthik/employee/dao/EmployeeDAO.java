package com.srikarthik.employee.dao;

import com.srikarthik.employee.config.DatabaseConnection;
import com.srikarthik.employee.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public Employee create(Employee employee) throws SQLException {
        String sql = """
                INSERT INTO employees
                (employee_code, first_name, last_name, email, phone,
                 department, designation, salary, hire_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, employee.getEmployeeCode());
            statement.setString(2, employee.getFirstName());
            statement.setString(3, employee.getLastName());
            statement.setString(4, employee.getEmail());
            statement.setString(5, employee.getPhone());
            statement.setString(6, employee.getDepartment());
            statement.setString(7, employee.getDesignation());
            statement.setBigDecimal(8, employee.getSalary());
            statement.setDate(9, Date.valueOf(employee.getHireDate()));

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    employee.setId(keys.getInt(1));
                }
            }
        }

        return employee;
    }

    public Employee findById(int id) throws SQLException {
        String sql = "SELECT * FROM employees WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        }

        return null;
    }

    public List<Employee> findAll() throws SQLException {
        String sql = "SELECT * FROM employees ORDER BY id";

        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                employees.add(mapRow(resultSet));
            }
        }

        return employees;
    }

    public boolean update(Employee employee) throws SQLException {
        String sql = """
                UPDATE employees
                SET employee_code = ?,
                    first_name = ?,
                    last_name = ?,
                    email = ?,
                    phone = ?,
                    department = ?,
                    designation = ?,
                    salary = ?,
                    hire_date = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, employee.getEmployeeCode());
            statement.setString(2, employee.getFirstName());
            statement.setString(3, employee.getLastName());
            statement.setString(4, employee.getEmail());
            statement.setString(5, employee.getPhone());
            statement.setString(6, employee.getDepartment());
            statement.setString(7, employee.getDesignation());
            statement.setBigDecimal(8, employee.getSalary());
            statement.setDate(9, Date.valueOf(employee.getHireDate()));
            statement.setInt(10, employee.getId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        }
    }

    private Employee mapRow(ResultSet resultSet) throws SQLException {
        Employee employee = new Employee();

        employee.setId(resultSet.getInt("id"));
        employee.setEmployeeCode(resultSet.getString("employee_code"));
        employee.setFirstName(resultSet.getString("first_name"));
        employee.setLastName(resultSet.getString("last_name"));
        employee.setEmail(resultSet.getString("email"));
        employee.setPhone(resultSet.getString("phone"));
        employee.setDepartment(resultSet.getString("department"));
        employee.setDesignation(resultSet.getString("designation"));
        employee.setSalary(resultSet.getBigDecimal("salary"));

        Date hireDate = resultSet.getDate("hire_date");
        if (hireDate != null) {
            employee.setHireDate(hireDate.toLocalDate());
        }

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            employee.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            employee.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return employee;
    }
}
