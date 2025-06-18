package com.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static final String CONFIG_FILE = "database.properties";
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "egor12082006";
    
    private final String url;
    private final String user;
    private final String password;

    public DatabaseManager() {
        Properties props = new Properties();
        String tempUrl = DEFAULT_URL;
        String tempUser = DEFAULT_USER;
        String tempPassword = DEFAULT_PASSWORD;
        
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            tempUrl = props.getProperty("db.url", DEFAULT_URL);
            tempUser = props.getProperty("db.user", DEFAULT_USER);
            tempPassword = props.getProperty("db.password", DEFAULT_PASSWORD);
        } catch (IOException e) {
            System.err.println("Не удалось загрузить настройки базы данных, используются значения по умолчанию");
        }
        
        this.url = tempUrl;
        this.user = tempUser;
        this.password = tempPassword;
    }

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
            System.err.println("URL: " + url);
            System.err.println("User: " + user);
            throw e;
        }
    }
} 