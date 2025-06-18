package Collection;

import java.util.TreeMap;
import java.time.LocalDateTime;
import Data.LabWork;
import java.util.Map;
import java.util.Iterator;
import java.util.Optional;
import Utilite.DatabaseManager;
import Utilite.UserManager;
import java.sql.SQLException;

public class CollectionManager {

    public Map<Integer, LabWork> getCollection() {
        return collection;
    }
    private final Map<Integer, LabWork> collection;
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime;
    private final DatabaseManager databaseManager;
    private final UserManager userManager;
    private boolean isDatabaseAvailable;

    public CollectionManager(DatabaseManager databaseManager, UserManager userManager) {
        this.collection = new TreeMap<>();
        this.lastInitTime = LocalDateTime.now();
        this.lastSaveTime = null;
        this.databaseManager = databaseManager;
        this.userManager = userManager;
        this.isDatabaseAvailable = databaseManager.isConnected();

        if (!isDatabaseAvailable) {
            System.err.println("Предупреждение: нет подключения к базе данных");
            System.err.println("Работа будет продолжена локально, без сохранения данных");
        }
    }

    public void loadFromDatabase() {
        if (!isDatabaseAvailable) {
            throw new IllegalStateException("Нет подключения к базе данных");
        }

        if (!userManager.isAuthenticated()) {
            throw new IllegalStateException("Необходимо войти в систему");
        }

        try {
            Map<Integer, LabWork> loadedCollection = databaseManager.loadCollection();
            if (loadedCollection == null) {
                throw new IllegalStateException("Не удалось загрузить данные из базы данных");
            }
            collection.clear();
            collection.putAll(loadedCollection);
            this.lastInitTime = LocalDateTime.now();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при загрузке данных из базы данных: " + e.getMessage(), e);
        }
    }

    public void loadUserData() {
        if (userManager.isAuthenticated()) {
            loadFromDatabase();
        }
    }
    public void saveToDatabase() {
        if (!isDatabaseAvailable) {
            throw new IllegalStateException("Нет подключения к базе данных");
        }

        if (!userManager.isAuthenticated()) {
            throw new IllegalStateException("Необходимо войти в систему");
        }

        try {
            databaseManager.saveCollection(collection, userManager.getCurrentUser().get().getId());
            this.lastSaveTime = LocalDateTime.now();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении в базу данных: " + e.getMessage(), e);
        }
    }

    public void clearCollection() {
        collection.clear();
    }

    public long countByMinimalPoint(double minimalPoint) {
        return collection.values().stream()
                .filter(labwork -> labwork.getMinimalPoint() != null)
                .filter(labwork -> labwork.getMinimalPoint() == minimalPoint)
                .count();
    }

    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }

    public String collectionType() {
        return collection.getClass().getName();
    }

    public int collectionSize() {
        return collection.size();
    }

    public boolean removeByKey(Integer key) {
        if (key == null || !collection.containsKey(key)) {
            return false;
        }
        collection.remove(key);
        return true;
    }

    public Optional<LabWork> getById(long id) {
        LabWork byKey = collection.get((int) id);
        if (byKey != null && byKey.getID() == id) {
            return Optional.of(byKey);
        }
        for (LabWork labWork : collection.values()) {
            if (labWork.getID() == id) {
                return Optional.of(labWork);
            }
        }
        return Optional.empty();
    }
    public boolean containsKey(int key) {
        return collection.containsKey(key);
    }

    public LabWork get(int key) {
        return collection.get(key);
    }

    public void put(int key, LabWork element) {
        collection.put(key, element);
    }

    public boolean insert(int key, LabWork lab) {
        if (lab == null) {
            return false;
        }

        collection.put(key, lab);
        return true;
    }

    public int removeKeysGreaterThan(int thresholdKey) {
        int count = 0;
        Iterator<Map.Entry<Integer, LabWork>> iterator = collection.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, LabWork> entry = iterator.next();
            if (entry.getKey() > thresholdKey) {
                iterator.remove();
                count++;
            }
        }
        return count;
    }
    private String saveFilePath;
    public String getSaveFilePath() {
        return this.saveFilePath;
    }

    public boolean isDatabaseAvailable() {
        return isDatabaseAvailable;
    }

}