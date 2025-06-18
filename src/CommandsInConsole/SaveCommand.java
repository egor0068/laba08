package CommandsInConsole;


import Collection.CollectionManager;
import Console.Console;

public class SaveCommand extends MainCommand {
    private final Console console;
    private final CollectionManager collectionManager;

    public SaveCommand(Console console, CollectionManager collectionManager) {
        super("save", "сохранить коллекцию в базу данных");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        if (!collectionManager.isDatabaseAvailable()) {
            console.println("Ошибка: нет подключения к базе данных");
            return false;
        }

        try {
            collectionManager.saveToDatabase();
            console.println("Коллекция успешно сохранена в базу данных");
            return true;
        } catch (Exception e) {
            console.println("Ошибка при сохранении коллекции: " + e.getMessage());
            return false;
        }
    }
}