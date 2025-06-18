package CommandsInConsole;

import Collection.CollectionManager;
import Console.Console;
import Data.Coordinates;
import Data.LabWork;
import Data.Discipline;

public class RemGreaterCommand extends MainCommand {
    private final Console console;
    private final CollectionManager collectionManager;
    private static final float MIN_PERSONAL_QUALITIES = 0.1f;
    private static final int MIN_LECTURE_HOURS = 1;
    private static final double MIN_VALUE = 0.0;

    public RemGreaterCommand(Console console, CollectionManager collectionManager) {
        super("remove_greater", "обнулить числовые поля элемента, если они больше указанного значения");
        this.console = console;
        this.collectionManager = collectionManager;
    }
    
    @Override
    public boolean apply(String[] arguments) {
        try {

            console.println("Введите ключ элемента: ");
            int key = Integer.parseInt(console.readLine().trim());

            if (!collectionManager.containsKey(key)) {
                console.println("Элемент с ключом " + key + " не найден");
                return false;
            }

            console.println("Введите пороговое значение (числа больше этого значения будут минимизированы): ");
            double threshold = Double.parseDouble(console.readLine().trim());

            LabWork lab = collectionManager.get(key);
            boolean modified = false;

            if (lab.getValue() != null && lab.getValue() > threshold) {
                lab.setValue(MIN_VALUE);
                modified = true;
            }
            
            if (lab.getCoordinates() != null) {
                Coordinates coords = lab.getCoordinates();
                if (coords.getX() > threshold) {
                    coords.setX(0);
                    modified = true;
                }
                if (coords.getY() > threshold) {
                    coords.setY(0L);
                    modified = true;
                }
            }
            
            if (lab.getMinimalPoint() != null && lab.getMinimalPoint() > threshold) {
                lab.setMinimalPoint(null);
                modified = true;
            }
            
            if (lab.getPersonalQualitiesMinimum() > threshold) {
                lab.setPersonalQualitiesMinimum(MIN_PERSONAL_QUALITIES);
                modified = true;
            }


            if (lab.getDiscipline() != null) {
                Discipline discipline = lab.getDiscipline();
                if (discipline.getLectureHours() > threshold) {
                    lab.setDiscipline(new Discipline(discipline.getName(), MIN_LECTURE_HOURS));
                    modified = true;
                }
            }
            
            if (modified) {
                collectionManager.put(key, lab);
                console.println("Значения элемента с ключом " + key + " успешно обновлены");
            } else {
                console.println("Нет значений больше " + threshold + " для минимизации");
            }
            
            return true;
            
        } catch (NumberFormatException e) {
            console.println("Ошибка! Введите корректное число");
            return false;
        } catch (IllegalArgumentException e) {
            console.println("Ошибка: " + e.getMessage());
            return false;
        }
    }
}