package CommandsInConsole;

import Collection.CollectionManager;
import Console.Console;
import Data.LabWork;

public class ShowCommand extends MainCommand {
    private final Console console;
    private final CollectionManager collectionManager;

    public ShowCommand(Console console, CollectionManager collectionManager) {
        super("show", "показать все элементы коллекции");
        this.console = console;
        this.collectionManager = collectionManager;
    }
    @Override
    public boolean apply(String[] arguments) {
        if (arguments.length > 1 && !arguments[1].isEmpty()) {
            console.println("Использование: " + getName());
            return false;
        }
        if (collectionManager.collectionSize() == 0) {
            console.println("Коллекция пуста.");
            return true;
        }
        console.println("\nВсего коллекций: " + collectionManager.collectionSize());
        collectionManager.getCollection().forEach((key, labWork) -> {
            console.println(" ");
            console.println("Ключ: " + key);
            printLabWorkDetails(labWork);
        });
        return true;
    }
    private void printLabWorkDetails(LabWork labWork) {
        console.println("ID: " + labWork.getID());
        console.println("Название: " + labWork.getName());
        console.println("Координаты по X: " + labWork.getCoordinates().getX());
        console.println("Координаты по Y: " + labWork.getCoordinates().getY());
        console.println("Дата создания: " + labWork.getCreationDate());
        console.println("Минимальный балл: " + labWork.getMinimalPoint());
        console.println("Личные качества: " + labWork.getPersonalQualitiesMinimum());
        console.println("Сложность: " + labWork.getDifficulty());
        console.println("Дисциплина(name___lectureHours): " + labWork.getDiscipline());
        console.println(" ");
    }
}