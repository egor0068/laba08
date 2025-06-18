package Utilite;

import Data.User;
import java.sql.*;
import java.util.Optional;

public class UserManager {
    private final DatabaseManager databaseManager;
    private User currentUser;

    public UserManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        initUserTable();
    }

    private void initUserTable() {
        try (Statement stmt = databaseManager.getConnection().createStatement()) {
            // Создаем таблицу пользователей, если она не существует
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "    id SERIAL PRIMARY KEY," +
                "    username VARCHAR(255) UNIQUE NOT NULL," +
                "    password_hash VARCHAR(255) NOT NULL" +
                ")"
            );
        } catch (SQLException e) {
            System.err.println("Ошибка при инициализации таблицы пользователей: " + e.getMessage());
        }
    }

    public boolean register(String username, String password) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            User tempUser = new User(username, password, 0); // id будет присвоен базой данных
            stmt.setString(1, username);
            stmt.setString(2, tempUser.getPasswordHash());
            stmt.executeUpdate();

            // Получаем сгенерированный id
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    currentUser = new User(username, password, rs.getInt(1));
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Код ошибки для нарушения уникальности
                System.err.println("Пользователь с таким именем уже существует");
            } else {
                System.err.println("Ошибка при регистрации пользователя: " + e.getMessage());
            }
            return false;
        }
    }

    public boolean login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(username, password, rs.getInt("id"));
                    if (user.getPasswordHash().equals(rs.getString("password_hash"))) {
                        currentUser = user;
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при входе в систему: " + e.getMessage());
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }
} 