package CommandsInConsole;

import Collection.CollectionManager;
import Console.Console;
import Data.LabWork;
import Data.Coordinates;
import Data.Discipline;

public class ReplaceLowNullCommand extends MainCommand {
    private final Console console;
    private final CollectionManager collection;

    public ReplaceLowNullCommand(Console console, CollectionManager collection) {
        super("replacelowNull", "заменить числовые поля элемента, если новые значения меньше существующих");
        this.console = console;
        this.collection = collection;
    }
    
    @Override
    public boolean apply(String[] args) {
        try {

            console.println("Введите ключ элемента: ");
            int key = Integer.parseInt(console.readLine().trim());

            if (!collection.containsKey(key)) {
                console.println("Элемент с ключом " + key + " не найден");
                return false;
            }
            
            LabWork oldElement = collection.get(key);
            LabWork newElement = new LabWork();
            boolean anyFieldReplaced = false;

            newElement.setID(oldElement.getID());
            newElement.setName(oldElement.getName());
            newElement.setCreationDate(oldElement.getCreationDate());
            newElement.setDifficulty(oldElement.getDifficulty());
            newElement.setCoordinates(oldElement.getCoordinates());
            newElement.setDiscipline(oldElement.getDiscipline());

            console.println("Введите новое значение для value (текущее: " + oldElement.getValue() + "): ");
            double newValue = Double.parseDouble(console.readLine().trim());
            if (newValue < oldElement.getValue()) {
                newElement.setValue(newValue);
                anyFieldReplaced = true;
                console.println("Значение value обновлено");
            } else {
                newElement.setValue(oldElement.getValue());
            }

            if (oldElement.getMinimalPoint() != null) {
                console.println("Введите новое значение для minimal point (текущее: " + oldElement.getMinimalPoint() + "): ");
                double newMinPoint = Double.parseDouble(console.readLine().trim());
                if (newMinPoint < oldElement.getMinimalPoint()) {
                    newElement.setMinimalPoint(newMinPoint);
                    anyFieldReplaced = true;
                    console.println("Значение minimal point обновлено");
                } else {
                    newElement.setMinimalPoint(oldElement.getMinimalPoint());
                }
            }

            console.println("Введите новое значение для personal qualities (текущее: " + oldElement.getPersonalQualitiesMinimum() + "): ");
            float newQuality = Float.parseFloat(console.readLine().trim());
            if (newQuality < oldElement.getPersonalQualitiesMinimum()) {
                newElement.setPersonalQualitiesMinimum(newQuality);
                anyFieldReplaced = true;
                console.println("Значение personal qualities обновлено");
            } else {
                newElement.setPersonalQualitiesMinimum(oldElement.getPersonalQualitiesMinimum());
            }

            Coordinates oldCoords = oldElement.getCoordinates();
            console.println("Введите новое значение для coordinates X (текущее: " + oldCoords.getX() + "): ");
            int newX = Integer.parseInt(console.readLine().trim());
            console.println("Введите новое значение для coordinates Y (текущее: " + oldCoords.getY() + "): ");
            long newY = Long.parseLong(console.readLine().trim());
            
            if (newX < oldCoords.getX() || newY < oldCoords.getY()) {
                newElement.setCoordinates(new Coordinates(
                    newX < oldCoords.getX() ? newX : oldCoords.getX(),
                    newY < oldCoords.getY() ? newY : oldCoords.getY()
                ));
                anyFieldReplaced = true;
                console.println("Значения координат обновлены");
            }

            if (oldElement.getDiscipline() != null) {
                console.println("Введите новое значение для lecture hours (текущее: " + 
                    oldElement.getDiscipline().getLectureHours() + "): ");
                int newHours = Integer.parseInt(console.readLine().trim());
                if (newHours < oldElement.getDiscipline().getLectureHours()) {
                    newElement.setDiscipline(new Discipline(
                        oldElement.getDiscipline().getName(),
                        newHours
                    ));
                    anyFieldReplaced = true;
                    console.println("Значение lecture hours обновлено");
                }
            }
            
            if (anyFieldReplaced) {
                collection.put(key, newElement);
                console.println("Элемент успешно обновлен");
                return true;
            } else {
                console.println("Ни одно значение не было обновлено (новые значения не меньше существующих)");
                return false;
            }
            
        } catch (NumberFormatException e) {
            console.println("Ошибка: введите корректное число");
            return false;
        } catch (IllegalArgumentException e) {
            console.println("Ошибка: " + e.getMessage());
            return false;
        } catch (Exception e) {
            console.println("Ошибка: " + e.getMessage());
            return false;
        }
    }
}