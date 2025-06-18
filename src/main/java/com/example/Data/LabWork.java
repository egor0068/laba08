package Data;
import java.time.*;
import java.util.*;

public class LabWork {

    private Integer id;
    private String name;
    private Coordinates coordinates;
    private java.time.LocalDateTime creationDate;
    private Double minimalPoint;
    private float personalQualitiesMinimum;
    private Difficulty difficulty;
    private Discipline discipline;
    private Double value;
    private Integer ownerId;

    public LabWork(Double value, String name, Coordinates coordinates,
                   LocalDateTime creationDate, Double minimalPoint,
                   float personalQualitiesMinimum, Difficulty difficulty,
                   Discipline discipline) {
        this.id = generateID();
        this.value = value;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.minimalPoint = minimalPoint;
        this.personalQualitiesMinimum = personalQualitiesMinimum;
        this.difficulty = difficulty;
        this.discipline = discipline;
        this.setName(name);
        this.creationDate = LocalDateTime.now();
        this.ownerId = null;
    }

    public LabWork(Integer id, Double value, String name, Coordinates coordinates,
                   LocalDateTime creationDate, Double minimalPoint,
                   float personalQualitiesMinimum, Difficulty difficulty,
                   Discipline discipline) {
        this.id = id;
        this.value = value;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.minimalPoint = minimalPoint;
        this.personalQualitiesMinimum = personalQualitiesMinimum;
        this.difficulty = difficulty;
        this.discipline = discipline;
        this.setName(name);
        this.ownerId = null;
    }

    public LabWork() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        this.name = name;
    }
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты не могут быть null");
        }
        this.coordinates = coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        if (creationDate == null) {
            throw new IllegalArgumentException("Дата создания не может быть null");
        }
        this.creationDate = creationDate;
    }

    public Double getMinimalPoint() {
        return minimalPoint;
    }

    public void setMinimalPoint(Double minimalPoint) {
        if (minimalPoint != null && minimalPoint <= 0) {
            throw new IllegalArgumentException("Минимальный балл должен быть больше 0");
        }
        this.minimalPoint = minimalPoint;
    }

    public float getPersonalQualitiesMinimum() {
        return personalQualitiesMinimum;
    }

    public void setPersonalQualitiesMinimum(float personalQualitiesMinimum) {
        if (personalQualitiesMinimum <= 0) {
            throw new IllegalArgumentException("Минимальные личные качества должны быть больше 0");
        }
        this.personalQualitiesMinimum = personalQualitiesMinimum;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID не может быть null или меньше/равно 0");
        }
        this.id = id;
    }

    public Integer generateID() {
        return Math.abs(new Random().nextInt()) + 1;
    }
    public Double getValue() {
        return this.value;
    }
    public void setValue(Double value) {
        this.value = value;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "LabWork{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", minimalPoint=" + minimalPoint +
                ",value=" + value +
                ", personalQualitiesMinimum=" + personalQualitiesMinimum +
                ", difficulty=" + difficulty +
                ", discipline=" + discipline +
                ", ownerId=" + ownerId +
                '}';
    }

}