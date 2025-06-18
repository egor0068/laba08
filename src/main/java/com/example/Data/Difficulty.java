package Data;

import Enum.*;
import java.util.Arrays;

public class Difficulty {
    private String name;
    public Difficulty(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }

    public static Difficulty valueOf(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Строка не может быть пустой или null");
        }
        String upperCaseInput = input.toUpperCase();
        for (DifficultyEnum enumValue : DifficultyEnum.values()) {
            if (enumValue.name().equals(upperCaseInput)) {

                return convertFromEnum(enumValue);
            }
        }throw new IllegalArgumentException("Неверное значение: " + input +
                ". Допустимые значения: " + Arrays.toString(DifficultyEnum.values()));
    }
    private static Difficulty convertFromEnum(DifficultyEnum enumValue) {
        switch (enumValue) {
            case IMPOSSIBLE: return new Difficulty("IMPOSSIBLE");
            case TERRIBLE: return new Difficulty("TERRIBLE");
            case VERY_HARD: return new Difficulty("VERY_HARD");
            default:
                throw new IllegalArgumentException("Неизвестное значение DifficultyEnum");
        }
    }
}
