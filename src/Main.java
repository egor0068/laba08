import Collection.CollectionManager;
import CommandsInConsole.*;
import Console.Console;
import Collection.CommandManager;
import Utilite.DatabaseManager;
import Utilite.UserManager;
import java.util.NoSuchElementException;

public class Main {
    public static void main(String[] args) {
        Console console = new Console();
        DatabaseManager databaseManager = new DatabaseManager();
        UserManager userManager = new UserManager(databaseManager);
        CollectionManager collectionManager = new CollectionManager(databaseManager, userManager);
        CommandManager commandManager = new CommandManager();

        commandManager.registerCommand(new HelpCommand(console, commandManager));
        commandManager.registerCommand(new LoginCommand(userManager, collectionManager));
        commandManager.registerCommand(new RegisterCommand(console, userManager));
        commandManager.registerCommand(new LogoutCommand(console, userManager));
        commandManager.registerCommand(new InfoCommand(console, collectionManager));
        commandManager.registerCommand(new ShowCommand(console, collectionManager));
        commandManager.registerCommand(new InsertCommand(collectionManager, console));
        commandManager.registerCommand(new UpdateCommand(console, collectionManager));
        commandManager.registerCommand(new RemKeyCommand(console, collectionManager));
        commandManager.registerCommand(new ClearCommand(collectionManager, console));
        commandManager.registerCommand(new ExecuteScriptCommand(console, collectionManager));
        commandManager.registerCommand(new ExitCommand(console));
        commandManager.registerCommand(new RemGreaterCommand(console, collectionManager));
        commandManager.registerCommand(new RemLowKeyNullCommand(console, collectionManager));
        commandManager.registerCommand(new CountMinimalPointCommand(console, collectionManager));
        commandManager.registerCommand(new ReplaceLowNullCommand(console, collectionManager));
        commandManager.registerCommand(new SaveCommand(console, collectionManager));

        commandManager.getCommand("help").setRequiresAuth(false);
        commandManager.getCommand("login").setRequiresAuth(false);
        commandManager.getCommand("register").setRequiresAuth(false);

        try {
            while (true) {

                if (userManager.isAuthenticated()) {
                    console.println("\nВы вошли как: " + userManager.getCurrentUser().get().getUsername());
                } else {
                    console.println("\nВы не авторизованы");
                }

                console.println("Доступные команды:");
                int index = 1;
                for (MainCommand command : commandManager.getCommands().values()) {

                    if (userManager.isAuthenticated() && 
                        (command.getName().equals("login") || command.getName().equals("register"))) {
                        continue;
                    }

                    if (!command.requiresAuth() || userManager.isAuthenticated()) {
                        console.println(index + ". " + command.getName());
                        index++;
                    }
                }
                
                String input;
                try {
                    input = console.readLine("\n$ ");
                } catch (NoSuchElementException e) {

                    console.println("\nПолучен сигнал EOF (Ctrl+D). Завершение программы...");

                    if (userManager.isAuthenticated()) {
                        try {
                            collectionManager.saveToDatabase();
                            console.println("Коллекция сохранена.");
                        } catch (Exception saveEx) {
                            console.println("Ошибка при сохранении коллекции: " + saveEx.getMessage());
                        }
                    }
                    break;
                }

                if (input == null || input.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    String[] parts = input.trim().split("\\s+", 2);
                    String commandName = parts[0];
                    String[] commandArgs = parts.length > 1 ? new String[]{parts[1]} : new String[0];

                    try {
                        int num = Integer.parseInt(commandName);
                        int currentIndex = 1;
                        MainCommand selectedCommand = null;

                        for (MainCommand command : commandManager.getCommands().values()) {

                            if (userManager.isAuthenticated() && 
                                (command.getName().equals("login") || command.getName().equals("register"))) {
                                continue;
                            }
                            
                            if (!command.requiresAuth() || userManager.isAuthenticated()) {
                                if (currentIndex == num) {
                                    selectedCommand = command;
                                    break;
                                }
                                currentIndex++;
                            }
                        }
                        
                        if (selectedCommand != null) {
                            commandName = selectedCommand.getName();
                        }
                    } catch (NumberFormatException ignored) {

                    }
                    
                    MainCommand command = commandManager.getCommand(commandName);
                    if (command == null) {
                        console.println("Неизвестная команда: " + commandName);
                        continue;
                    }

                    if (command.requiresAuth() && !userManager.isAuthenticated()) {
                        console.println("Для выполнения этой команды необходимо войти в систему");
                        continue;
                    }

                    command.apply(commandArgs);
                } catch (Exception e) {
                    console.println("Ошибка: " + e.getMessage());
                }
            }
        } finally {

            databaseManager.close();
        }
    }
}