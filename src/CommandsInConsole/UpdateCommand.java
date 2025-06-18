package CommandsInConsole;


import Collection.CollectionManager;
import Console.Console;
import Data.LabWork;
import java.util.Optional;

public class UpdateCommand extends MainCommand {
    private final Console console;
    private final CollectionManager collectionManager;

    public UpdateCommand(Console console, CollectionManager collectionManager) {
        super("update", "изменить ID элемента в памяти");
        this.console = console;
        this.collectionManager = collectionManager;
    }
    
    @Override
    public boolean apply(String[] arguments) {
        try {

            console.println("Введите текущий ID элемента: ");
            int oldId = Integer.parseInt(console.readLine().trim());

            Optional<LabWork> labWorkOpt = collectionManager.getById(oldId);
            if (!labWorkOpt.isPresent()) {
                console.println("Элемент с ID " + oldId + " не найден");
                return false;
            }

            LabWork labWork = labWorkOpt.get();
            console.println("Найден элемент: " + labWork.getName() + " (ID: " + oldId + ")");

            console.println("Введите новый ID (положительное число): ");
            int newId = Integer.parseInt(console.readLine().trim());

            if (newId <= 0) {
                console.println("ID должен быть положительным числом!");
                return false;
            }

            if (collectionManager.getById(newId).isPresent()) {
                console.println("Элемент с ID " + newId + " уже существует!");
                return false;
            }

            labWork.setID(newId);
            console.println("ID успешно изменён на " + newId);
            console.println("Для сохранения изменений в базу данных используйте команду save");
            return true;

        } catch (NumberFormatException e) {
            console.println("Ошибка: ID должен быть целым числом");
            return false;
        } catch (IllegalArgumentException e) {
            console.println("Ошибка: " + e.getMessage());
            return false;
        }
    }
}