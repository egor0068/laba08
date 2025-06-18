package CommandsInConsole;

import Collection.CollectionManager;
import Console.Console;

public class RemKeyCommand extends MainCommand {
    private final Console console;
    private final CollectionManager collection;

    public RemKeyCommand(Console console, CollectionManager collection) {
        super("remove_key", "удалить элемент по указанному ключу");
        this.console = console;
        this.collection = collection;
    }
    @Override
    public boolean apply(String[] arguments) {
        String keyInput = getKeyInput(arguments);
        try {
            int key = Integer.parseInt(keyInput.trim());
            boolean removed = collection.removeByKey(key);
            if (removed) {
                console.println("Элемент с ключом " + key + " успешно удален");
                return true;
            } else {
                console.println("Элемент с ключом " + key + " не найден");
                return false;
            }
        } catch (NumberFormatException e) {
            console.println("Ошибка: ключ должен быть целым числом");
            return false;
        }
    }
    private String getKeyInput(String[] arguments) {
        if (arguments.length > 1) {
            return arguments[1];
        }
        console.println("Введите ключ элемента для удаления: ");
        return console.readLine();
    }
}
