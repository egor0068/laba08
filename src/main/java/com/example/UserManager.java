package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import Utilite.DatabaseManager;

public class UserManager {
    private final DatabaseManager databaseManager;
    private User currentUser;

    public UserManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        createUsersTable();
    }

    private void createUsersTable() {
        try (Connection conn = databaseManager.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                        "id SERIAL PRIMARY KEY, " +
                        "username VARCHAR(50) UNIQUE NOT NULL, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(String username, String password) {
        try (Connection conn = databaseManager.getConnection()) {
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Optional<User> authenticateUser(String username, String password) {
        try (Connection conn = databaseManager.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        currentUser = new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                        );
                        return Optional.of(currentUser);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean isUsernameTaken(String username) {
        try (Connection conn = databaseManager.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

} 