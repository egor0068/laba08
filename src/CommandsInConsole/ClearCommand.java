package CommandsInConsole;

import Collection.CollectionManager;
import Console.Console;

public class ClearCommand extends MainCommand {
    private final CollectionManager collectionManager;
    private final Console console;

    public ClearCommand(CollectionManager collectionManager, Console console) {
        super("clear", "очистить коллекцию");
        this.collectionManager = collectionManager;
        this.console = console;
    }
    @Override
    public boolean apply(String[] arguments) {
        if (arguments.length > 1 && !arguments[1].isEmpty()) {
            console.println("Использование: " + getName());
            return false;
        }
        if ("clear".equalsIgnoreCase(getName())) {
            collectionManager.clearCollection();
            console.println("Коллекция очищена.");
            return true;
        }
        return false;
    }
}