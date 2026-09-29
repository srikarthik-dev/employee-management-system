package com.srikarthik.employee.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        // ------------------------------------------------------------------
        // Priority 1: application.properties on the classpath
        //   → used for local / IDE development (file is git-ignored).
        // Priority 2: environment variables DB_URL / DB_USERNAME / DB_PASSWORD
        //   → used inside the Docker container where no properties file is
        //     bundled into the image.
        // ------------------------------------------------------------------
        String url      = null;
        String username = null;
        String password = null;

        try (InputStream input = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input != null) {
                Properties properties = new Properties();
                properties.load(input);
                url      = properties.getProperty("db.url");
                username = properties.getProperty("db.username");
                password = properties.getProperty("db.password");
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }

        // Fall back to environment variables when properties file is absent
        // (e.g. inside the Docker container).
        if (url == null || url.isBlank()) {
            url = System.getenv("DB_URL");
        }
        if (username == null || username.isBlank()) {
            username = System.getenv("DB_USERNAME");
        }
        if (password == null || password.isBlank()) {
            // An empty/blank password is a valid value, so only substitute
            // when the property was not present at all.
            String envPassword = System.getenv("DB_PASSWORD");
            password = (envPassword != null) ? envPassword : "";
        }

        if (url == null || url.isBlank()) {
            throw new RuntimeException(
                "Database URL not configured. " +
                "Provide src/main/resources/application.properties " +
                "or set the DB_URL environment variable.");
        }
        if (username == null || username.isBlank()) {
            throw new RuntimeException(
                "Database username not configured. " +
                "Provide src/main/resources/application.properties " +
                "or set the DB_USERNAME environment variable.");
        }

        URL      = url;
        USERNAME = username;
        PASSWORD = password;
    }

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
