package com.srikarthik.employee;

import com.srikarthik.employee.config.DatabaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void shouldConnectToDatabase() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());

            System.out.println("Database connection successful!");
            System.out.println("Database: " +
                    connection.getCatalog());
        }
    }
}
