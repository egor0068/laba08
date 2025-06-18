package CommandsInConsole;

import Collection.CollectionManager;
import Collection.CommandManager;
import Console.Console;
import Data.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.Random;

public class ExecuteScriptCommand extends MainCommand {
    private final Console console;
    private final CollectionManager collectionManager;
    private static final TreeMap<String, Boolean> executingScripts = new TreeMap<>();

    public ExecuteScriptCommand(Console console, CollectionManager collectionManager) {
        super("execute_script", "выполнить скрипт из указанного файла");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        String fileName;
        if (arguments.length == 0) {
            console.println("Введите имя файла скрипта:");
            fileName = console.readLine().trim();
            if (fileName.isEmpty()) {
                console.println("Ошибка: имя файла не может быть пустым");
                return false;
            }
        } else {
            fileName = arguments[0];
        }

        File scriptFile = new File(fileName);

        if (executingScripts.containsKey(fileName)) {
            console.println("Ошибка: рекурсивный вызов скрипта '" + fileName + "'");
            return false;
        }

        if (!scriptFile.exists() || !scriptFile.canRead()) {
            console.println("Ошибка: файл '" + fileName + "' не найден или недоступен");
            return false;
        }

        executingScripts.put(fileName, true);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(scriptFile), StandardCharsets.UTF_8))) {

            console.println("Выполнение скрипта: " + scriptFile.getAbsolutePath());

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                String[] parts = line.split("\\s+", 2);
                String commandName = parts[0].toLowerCase();

                if (commandName.equals("insert")) {
                    try {

                        LabWork labWork = readLabWorkFromScript(reader);
                        if (labWork != null) {

                            int newKey = generateUniqueKey();
                            if (collectionManager.insert(newKey, labWork)) {
                                console.println("Элемент с автоматически сгенерированным ключом " + newKey + " успешно добавлен");
                            } else {
                                console.println("Ошибка при добавлении элемента");
                            }
                        }
                    } catch (Exception e) {
                        console.println("Ошибка при чтении данных из скрипта: " + e.getMessage());
                    }
                    continue;
                }

                if (commandName.equals("clear")) {
                    try {
                        ClearCommand clearCommand = new ClearCommand(collectionManager, console);
                        clearCommand.apply(new String[0]);
                        continue;
                    } catch (Exception e) {
                        console.println("Ошибка при выполнении команды clear: " + e.getMessage());
                        continue;
                    }
                }

                try {
                    CommandManager commandManager = new CommandManager();
                    commandManager.registerCommand(new HelpCommand(console, commandManager));
                    commandManager.registerCommand(new InfoCommand(console, collectionManager));
                    commandManager.registerCommand(new ShowCommand(console, collectionManager));
                    commandManager.registerCommand(new ExitCommand(console));
                    commandManager.registerCommand(new SaveCommand(console, collectionManager));
                    
                    MainCommand command = commandManager.getCommand(commandName);
                    if (command != null) {
                        String[] cmdArgs = parts.length > 1 ? new String[]{parts[1]} : new String[0];
                        console.println("Выполнение команды: " + commandName);
                        command.apply(cmdArgs);
                    } else {
                        console.println("Неизвестная команда: " + commandName);
                    }
                } catch (Exception e) {
                    console.println("Ошибка при выполнении команды " + commandName + ": " + e.getMessage());
                }
            }

            console.println("Скрипт успешно выполнен: " + fileName);
            return true;

        } catch (IOException e) {
            console.println("Ошибка выполнения скрипта: " + e.getMessage());
            return false;
        } finally {
            executingScripts.remove(fileName);
        }
    }

    private LabWork readLabWorkFromScript(BufferedReader reader) throws IOException {
        try {
            String name = readNonEmptyLine(reader);
            int x = Integer.parseInt(readNonEmptyLine(reader));
            long y = Long.parseLong(readNonEmptyLine(reader));
            Coordinates coordinates = new Coordinates(x, y);

            String minimalPointStr = readNonEmptyLine(reader);
            Double minimalPoint = minimalPointStr.equalsIgnoreCase("null") ? null : Double.parseDouble(minimalPointStr);
            
            Double value = Double.parseDouble(readNonEmptyLine(reader));
            float personalQualitiesMinimum = Float.parseFloat(readNonEmptyLine(reader));

            String difficultyStr = readNonEmptyLine(reader);
            Difficulty difficulty = difficultyStr.equalsIgnoreCase("null") ? null : Difficulty.valueOf(difficultyStr);

            String disciplineName = readNonEmptyLine(reader);
            int lectureHours = Integer.parseInt(readNonEmptyLine(reader));
            Discipline discipline = new Discipline(disciplineName, lectureHours);

            return new LabWork(value, name, coordinates, LocalDateTime.now(),
                    minimalPoint, personalQualitiesMinimum, difficulty, discipline);

        } catch (Exception e) {
            console.println("Ошибка при чтении параметров LabWork: " + e.getMessage());
            return null;
        }
    }

    private String readNonEmptyLine(BufferedReader reader) throws IOException {
        String line;
        do {
            line = reader.readLine();
            if (line == null) {
                throw new IOException("Неожиданный конец файла при чтении параметров");
            }
            line = line.trim();
        } while (line.isEmpty() || line.startsWith("//"));
        return line;
    }

    private int generateUniqueKey() {
        int key;
        Random random = new Random();
        do {
            key = 10000 + random.nextInt(90000);
        } while (collectionManager.containsKey(key));
        return key;
    }
}