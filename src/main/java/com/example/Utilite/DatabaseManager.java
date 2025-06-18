package Utilite;

import java.sql.*;
import Data.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class DatabaseManager {
    // Строка подключения к базе данных PostgreSQL через SSH туннель
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres"; // Локальный адрес через SSH туннель
    private static final String USER = "postgres";  // Имя пользователя
    private static final String PASSWORD = "egor12082006";  // Пароль для доступа к БД

    private Connection connection;
    private boolean isConnected = false;

    private static final String CREATE_USERS_TABLE = 
        "CREATE TABLE IF NOT EXISTS users (" +
        "    id SERIAL PRIMARY KEY," +
        "    username VARCHAR(255) UNIQUE NOT NULL," +
        "    password_hash VARCHAR(255) NOT NULL" +
        ")";
    
    private static final String CREATE_LABWORKS_TABLE = 
        "CREATE TABLE IF NOT EXISTS labworks (" +
        "    id INTEGER NOT NULL," +
        "    key INTEGER UNIQUE NOT NULL," +
        "    name VARCHAR(255) NOT NULL," +
        "    coordinates_x INTEGER NOT NULL," +
        "    coordinates_y BIGINT NOT NULL," +
        "    creation_date TIMESTAMP NOT NULL," +
        "    minimal_point DOUBLE PRECISION," +
        "    value DOUBLE PRECISION NOT NULL," +
        "    personal_qualities_minimum REAL NOT NULL," +
        "    difficulty VARCHAR(20)," +
        "    discipline_name VARCHAR(255)," +
        "    lecture_hours INTEGER," +
        "    owner_id INTEGER REFERENCES users(id)" +
        ")";

    public DatabaseManager() {
        connect();
    }

    private void connect() {
        try {
            // Регистрируем драйвер JDBC
            Class.forName("org.postgresql.Driver");
            
            // Устанавливаем соединение
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            isConnected = true;
            System.out.println("Успешное подключение к базе данных");
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("Не найден драйвер PostgreSQL JDBC: " + e.getMessage());
            isConnected = false;
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
            System.err.println("URL: " + URL);
            System.err.println("Пользователь: " + USER);
            isConnected = false;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_USERS_TABLE);
            stmt.execute(CREATE_LABWORKS_TABLE);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void saveCollection(Map<Integer, LabWork> collection, int userId) throws SQLException {
        if (!isConnected) {
            throw new SQLException("Нет подключения к базе данных");
        }

        connection.setAutoCommit(false);
        try {
            // Удаляем только записи текущего пользователя
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM labworks WHERE owner_id = ?")) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            // Получаем максимальный существующий ключ
            int maxKey = 0;
            try (PreparedStatement stmt = connection.prepareStatement("SELECT MAX(key) FROM labworks")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    maxKey = rs.getInt(1);
                }
            }

            // Подготавливаем запрос для вставки
            String labWorkSQL = 
                "INSERT INTO labworks (" +
                "    id, key, name, coordinates_x, coordinates_y, creation_date," +
                "    minimal_point, value, personal_qualities_minimum," +
                "    difficulty, discipline_name, lecture_hours, owner_id" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement labWorkStmt = connection.prepareStatement(labWorkSQL)) {
                // Сохраняем только элементы текущего пользователя
                for (Map.Entry<Integer, LabWork> entry : collection.entrySet()) {
                    LabWork lab = entry.getValue();
                    // Проверяем, принадлежит ли элемент текущему пользователю
                    if (lab.getOwnerId() != null && lab.getOwnerId() == userId) {
                        // Генерируем новый уникальный ключ
                        maxKey++;
                        Integer newKey = maxKey;

                        labWorkStmt.setInt(1, lab.getID());
                        labWorkStmt.setInt(2, newKey);
                        labWorkStmt.setString(3, lab.getName());
                        labWorkStmt.setInt(4, lab.getCoordinates().getX());
                        labWorkStmt.setLong(5, lab.getCoordinates().getY());
                        labWorkStmt.setTimestamp(6, Timestamp.valueOf(lab.getCreationDate()));
                        if (lab.getMinimalPoint() != null) {
                            labWorkStmt.setDouble(7, lab.getMinimalPoint());
                        } else {
                            labWorkStmt.setNull(7, Types.DOUBLE);
                        }
                        labWorkStmt.setDouble(8, lab.getValue());
                        labWorkStmt.setFloat(9, lab.getPersonalQualitiesMinimum());
                        if (lab.getDifficulty() != null) {
                            labWorkStmt.setString(10, lab.getDifficulty().toString());
                        } else {
                            labWorkStmt.setNull(10, Types.VARCHAR);
                        }
                        if (lab.getDiscipline() != null) {
                            labWorkStmt.setString(11, lab.getDiscipline().getName());
                            labWorkStmt.setInt(12, lab.getDiscipline().getLectureHours());
                        } else {
                            labWorkStmt.setNull(11, Types.VARCHAR);
                            labWorkStmt.setNull(12, Types.INTEGER);
                        }
                        labWorkStmt.setInt(13, userId);
                        labWorkStmt.executeUpdate();
                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public Map<Integer, LabWork> loadCollection() throws SQLException {
        if (!isConnected) {
            throw new SQLException("Нет подключения к базе данных");
        }

        Map<Integer, LabWork> collection = new TreeMap<>();
        
        String sql = "SELECT * FROM labworks ORDER BY key";
        System.out.println("Loading collection from database...");
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        int key = rs.getInt("key");
                        int id = rs.getInt("id");
                        int ownerId = rs.getInt("owner_id");
                        System.out.println("Loading element - key: " + key + ", id: " + id + ", ownerId: " + ownerId);
                        
                        Coordinates coordinates = new Coordinates(
                            rs.getInt("coordinates_x"),
                            rs.getLong("coordinates_y")
                        );
                        Discipline discipline = null;
                        String disciplineName = rs.getString("discipline_name");
                        Integer lectureHours = rs.getObject("lecture_hours", Integer.class);
                        if (disciplineName != null && lectureHours != null) {
                            discipline = new Discipline(disciplineName, lectureHours);
                        }
                        String difficultyStr = rs.getString("difficulty");
                        Difficulty difficulty = null;
                        if (difficultyStr != null) {
                            difficulty = Difficulty.valueOf(difficultyStr);
                        }
                        LabWork labWork = new LabWork(
                            id,
                            rs.getDouble("value"),
                            rs.getString("name"),
                            coordinates,
                            rs.getTimestamp("creation_date").toLocalDateTime(),
                            rs.getObject("minimal_point", Double.class),
                            rs.getFloat("personal_qualities_minimum"),
                            difficulty,
                            discipline
                        );
                        labWork.setOwnerId(ownerId);
                        System.out.println("Created LabWork object with ownerId: " + labWork.getOwnerId());
                        collection.put(key, labWork);
                    } catch (Exception e) {
                        System.err.println("Ошибка при загрузке объекта: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("Collection loaded, total elements: " + collection.size());
        return collection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                isConnected = false;
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии соединения с базой данных: " + e.getMessage());
            }
        }
    }

    public void insertLabWork(LabWork lab, int key, int userId) throws SQLException {
        if (!isConnected) {
            throw new SQLException("Нет подключения к базе данных");
        }

        String labWorkSQL = 
            "INSERT INTO labworks (" +
            "    id, key, name, coordinates_x, coordinates_y, creation_date," +
            "    minimal_point, value, personal_qualities_minimum," +
            "    difficulty, discipline_name, lecture_hours, owner_id" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement labWorkStmt = connection.prepareStatement(labWorkSQL)) {
            labWorkStmt.setInt(1, lab.getID());
            labWorkStmt.setInt(2, key);
            labWorkStmt.setString(3, lab.getName());
            labWorkStmt.setInt(4, lab.getCoordinates().getX());
            labWorkStmt.setLong(5, lab.getCoordinates().getY());
            labWorkStmt.setTimestamp(6, Timestamp.valueOf(lab.getCreationDate()));
            if (lab.getMinimalPoint() != null) {
                labWorkStmt.setDouble(7, lab.getMinimalPoint());
            } else {
                labWorkStmt.setNull(7, Types.DOUBLE);
            }
            labWorkStmt.setDouble(8, lab.getValue());
            labWorkStmt.setFloat(9, lab.getPersonalQualitiesMinimum());
            if (lab.getDifficulty() != null) {
                labWorkStmt.setString(10, lab.getDifficulty().toString());
            } else {
                labWorkStmt.setNull(10, Types.VARCHAR);
            }
            if (lab.getDiscipline() != null) {
                labWorkStmt.setString(11, lab.getDiscipline().getName());
                labWorkStmt.setInt(12, lab.getDiscipline().getLectureHours());
            } else {
                labWorkStmt.setNull(11, Types.VARCHAR);
                labWorkStmt.setNull(12, Types.INTEGER);
            }
            labWorkStmt.setInt(13, userId);
            labWorkStmt.executeUpdate();
        }
    }

    public void updateLabWork(LabWork lab) throws SQLException {
        if (!isConnected) {
            throw new SQLException("Нет подключения к базе данных");
        }

        String sql = "UPDATE labworks SET " +
            "name = ?, coordinates_x = ?, coordinates_y = ?, " +
            "minimal_point = ?, value = ?, personal_qualities_minimum = ?, " +
            "difficulty = ?, discipline_name = ?, lecture_hours = ? " +
            "WHERE id = ? AND owner_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, lab.getName());
            stmt.setInt(2, lab.getCoordinates().getX());
            stmt.setLong(3, lab.getCoordinates().getY());
            if (lab.getMinimalPoint() != null) {
                stmt.setDouble(4, lab.getMinimalPoint());
            } else {
                stmt.setNull(4, Types.DOUBLE);
            }
            stmt.setDouble(5, lab.getValue());
            stmt.setFloat(6, lab.getPersonalQualitiesMinimum());
            if (lab.getDifficulty() != null) {
                stmt.setString(7, lab.getDifficulty().toString());
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }
            if (lab.getDiscipline() != null) {
                stmt.setString(8, lab.getDiscipline().getName());
                stmt.setInt(9, lab.getDiscipline().getLectureHours());
            } else {
                stmt.setNull(8, Types.VARCHAR);
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setInt(10, lab.getID());
            stmt.setInt(11, lab.getOwnerId());
            stmt.executeUpdate();
        }
    }

    public void removeLabWork(Integer id) throws SQLException {
        if (!isConnected) {
            throw new SQLException("Нет подключения к базе данных");
        }

        String sql = "DELETE FROM labworks WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void clearCollection(int userId) throws SQLException {
        if (!isConnected) {
            throw new SQLException("Нет подключения к базе данных");
        }

        String sql = "DELETE FROM labworks WHERE owner_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
} 