package CommandsInConsole;

import Collection.CollectionManager;
import Console.Console;

public class RemLowKeyNullCommand extends MainCommand {
    private final Console console;
    private final CollectionManager collectionManager;

    public RemLowKeyNullCommand(Console console, CollectionManager collectionManager) {
        super("remove_lower_key_null", "удаляет элементы с ключами, превышающими заданный");
        this.console = console;
        this.collectionManager = collectionManager;
    }
    @Override
    public boolean apply(String[] arguments) {
        try {
            int key = requestKey();
            int removedCount = collectionManager.removeKeysGreaterThan(key);
            console.println("Удалено элементов с ключами > " + key + ": " + removedCount);
            return true;
        } catch (NumberFormatException e) {
            console.println("Ошибка! Ключ должен быть целым числом.");
            return false;
        }
    }
    private int requestKey() throws NumberFormatException {
        console.println("Введите ключ для сравнения (целое число): ");
        String input = console.readLine().trim();
        return Integer.parseInt(input);
    }
}