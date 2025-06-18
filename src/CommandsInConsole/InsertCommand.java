package CommandsInConsole;

import Collection.CollectionManager;
import java.util.Scanner;
import Console.Console;
import Data.LabWork;
import Data.Coordinates;
import Data.Discipline;
import Data.Difficulty;
import Enum.DifficultyEnum;
import java.time.LocalDateTime;
import java.util.Arrays;

public class InsertCommand extends MainCommand {
    private final CollectionManager collectionManager;
    private final Console console;

    public InsertCommand(CollectionManager collectionManager, Console console) {
        super("insert", "добавить элемент с заданным ключом");
        this.collectionManager = collectionManager;
        this.console = console;
    }
    @Override
    public boolean apply(String[] arguments) {
        try {
            int key = labKey();
            if (collectionManager.containsKey(key)) {
                console.println("Ошибка: Элемент с ключом " + key + " уже существует");
                return false;
            }
            String name = labName("Название лабораторной работы");
            String disciplineName = disciplineName("Название дисциплины");
            Double value = value("Оценка");
            Coordinates coordinates = coordinates();
            Double minimalPoint = minimalPoint("Минимальный балл");
            float personalQualitiesMinimum = qualitiesMinimum("Минимальные личные качества");
            Difficulty difficulty = difficulty();
            int lectureHours = lectureHours();
            Discipline discipline = new Discipline(disciplineName, lectureHours);

            LabWork labWork = new LabWork(
                    value,
                    name,
                    coordinates,
                    LocalDateTime.now(),
                    minimalPoint,
                    personalQualitiesMinimum,
                    difficulty,
                    discipline
            );
            collectionManager.insert(key, labWork);
            console.println("\nЭлемент добавлен в коллекцию, чтобы сохранить полученную коллекцию в файл - используйте команду save");
            console.println(" ");
            return true;
        } catch (IllegalArgumentException e) {
            console.println("Ошибка: " + e.getMessage());
            return false;
        }
    }
    private int labKey() {
        while (true) {
            try {
                System.out.print("Введите ключ элемента: ");
                Scanner scan = new Scanner(System.in);
                String input = scan.nextLine().trim();

                return Integer.parseInt(input);
            }
            catch(NumberFormatException e){
                console.println("Ошибка: Ключ должен быть целым числом");
            }
        }
    }
    private String disciplineName(String text) {
        while (true) {
            String input = console.readLine(text + ": ").trim();
            if (!input.isEmpty()) {
                return input;
            }
            console.println("Ошибка: Поле не может быть пустым");
        }
    }
    private String labName(String text) {
        while (true) {
            String input = console.readLine(text + ": ").trim();
            if (!input.isEmpty()) {
                return input;
            }
            console.println("Ошибка: Поле не может быть пустым");
        }
    }
    private Coordinates coordinates() {
        while (true) {
            try {
                console.println("Введите координаты:");
                int x = CoordinatesInteger("Координата X (целое число > 0)");
                long y = CoordinatesLong("Координата Y (целое число)");
                return new Coordinates(x, y);
            } catch (IllegalArgumentException e) {
                console.println("Ошибка: " + e.getMessage());
            }
        }
    }
    private int CoordinatesInteger(String text) {
        while (true) {
            try {
                String input = console.readLine(text + ": ").trim();
                int value = Integer.parseInt(input);
                if (value <= 0) {
                    throw new IllegalArgumentException("Значение должно быть больше 0");
                }
                return value;
            } catch (NumberFormatException e) {
                console.println("Ошибка: Введите целое число");
            }
        }
    }
    private long CoordinatesLong(String text) {
        while (true) {
            try {
                String input = console.readLine(text + ": ").trim();
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                console.println("Ошибка: Введите целое число");
            }
        }
    }
    private Double value(String text) {
        while (true) {
            try {
                String input = console.readLine(text + ": ").trim();
                double value = Double.parseDouble(input);
                if (value <= 0) {
                    throw new IllegalArgumentException("Значение должно быть положительным");
                }
                return value;
            } catch (NumberFormatException e) {
                console.println("Ошибка: Введите число");
            }
        }
    }
    private Double minimalPoint(String text) {
        while (true) {
            try {
                String input = console.readLine(text + ": ").trim();
                double value = Double.parseDouble(input);
                if (value <= 0) {
                    console.println("Ошибка: Значение должно быть положительным");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                console.println("Ошибка: Введите число");
            }
        }
    }
    private float qualitiesMinimum(String text) {
        while (true) {
            try {String input = console.readLine(text + ": ").trim();
                float value = Float.parseFloat(input);
                if (value <= 0) {
                    throw new IllegalArgumentException("Значение должно быть положительным");
                }
                return value;
            } catch (NumberFormatException e) {
                console.println("Ошибка: Введите число");
            }
        }
    }
    private Difficulty difficulty() {
        console.println("Доступные уровни сложности: " + Arrays.toString(DifficultyEnum.values()));
        while (true) {
            try {
                String input = console.readLine("Уровень сложности (можно пропустить): ").trim();
                if (input.isEmpty()) {
                    return null;
                }
                return Difficulty.valueOf(input);
            } catch (IllegalArgumentException e) {
                console.println("Ошибка: Введите одно из: " + Arrays.toString(DifficultyEnum.values()));
            }
        }
    }
    private int lectureHours() {
        while (true) {
            try {
                String input = console.readLine("Количество лекторских часов: ").trim();
                int hours = Integer.parseInt(input);
                if (hours <= 0) {
                    throw new IllegalArgumentException("Количество часов должно быть положительным");
                }
                return hours;
            } catch (NumberFormatException e) {
                console.println("Ошибка: Введите целое число");
            }
        }
    }
}
