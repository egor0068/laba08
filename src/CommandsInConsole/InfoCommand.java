package CommandsInConsole;

import Collection.CollectionManager;
import Console.Console;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

public class InfoCommand extends MainCommand {
    private final Console console;
    private final CollectionManager collectionManager;
    private final LocalDateTime programStartTime;

    public InfoCommand(Console console, CollectionManager collectionManager) {
        super("info", "вывести информацию о коллекции");
        this.console = console;
        this.collectionManager = collectionManager;
        this.programStartTime = LocalDateTime.now();
    }
    @Override
    public boolean apply(String[] arguments) {
        if (arguments.length > 1 && !arguments[1].isEmpty()) {
            console.println("Использование: " + getName());
            return false;
        }
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        console.println("Информация о коллекции:");
        console.println("Тип коллекции: " + collectionManager.collectionType());
        console.println("Тип элементов: LabWork");
        console.println("Количество элементов: " + collectionManager.collectionSize());

        LocalDateTime initTime = collectionManager.getLastInitTime();
        console.println("Дата последней инициализации: " +
                (initTime == null ? "не инициализирована" : initTime.format(dateFormat)));

        LocalDateTime saveTime = collectionManager.getLastSaveTime();
        console.println("Дата последнего сохранения: " +
                (saveTime == null ? "не сохранена" : saveTime.format(dateFormat)));

        String saveFilePath = collectionManager.getSaveFilePath();
        console.println("Файл сохранения коллекции: " +
                (saveFilePath == null ? "не указан (используйте 'save')" : saveFilePath));

        Duration uptime = Duration.between(programStartTime, LocalDateTime.now());
        long hours = uptime.toHours();
        long minutes = uptime.toMinutes() % 60;
        long seconds = uptime.getSeconds() % 60;
        console.println("Время работы программы: " +
                String.format("%d ч. %d мин. %d сек.", hours, minutes, seconds));
        return true;
    }
}