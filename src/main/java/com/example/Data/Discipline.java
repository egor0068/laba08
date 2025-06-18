package Data;

import java.util.Objects;

public class Discipline {
    private String name;
    private Integer lectureHours;
    public Discipline(String name, Integer lectureHours) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя дисциплины не может быть null или пустым.");
        }
        if (lectureHours == null) {
            throw new IllegalArgumentException("Количество лекционных часов не может быть null.");
        }
        this.name = name;
        this.lectureHours = lectureHours;
    }
    public String getName() {
        return name;
    }

    public Integer getLectureHours() {
        return lectureHours;
    }

    @Override
    public String toString() {
        return  name + "___" + lectureHours;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lectureHours);
    }
}