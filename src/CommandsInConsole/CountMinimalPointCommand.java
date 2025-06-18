package CommandsInConsole;

import Collection.CollectionManager;
import Console.Console;

public class CountMinimalPointCommand extends MainCommand {
    private final Console console;
    private final CollectionManager collection;

    public CountMinimalPointCommand(Console console, CollectionManager collection) {
        super("countMinimalPoint", "вывести количество элементов, значение поля minimalPoint которых равно заданному");
        this.console = console;
        this.collection = collection;
    }
    @Override
    public boolean apply(String[] arguments) {
        try {
            String minimalPointString;
            if (arguments.length >= 2) {
                minimalPointString = arguments[1];
            } else {
                minimalPointString = askForMinPoint();
            }
            double minimalPoint = Double.parseDouble(minimalPointString);
            long count = collection.countByMinimalPoint(minimalPoint);
            console.println("Найдено элементов: " + count);
            return true;
        } catch (NumberFormatException e) {
            console.println("Ошибка: необходимо ввести число");
            return false;
        }
    }
    private String askForMinPoint() {
        console.println("Введите минимальный балл для поиска: ");
        return console.readLine();
    }
}
