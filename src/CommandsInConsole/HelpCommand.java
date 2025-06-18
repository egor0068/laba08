package CommandsInConsole;

import Collection.CommandManager;
import Console.Console;
import java.util.Map;

public class HelpCommand extends MainCommand {
    private final Console console;
    private final CommandManager commandManager;

    public HelpCommand(Console console, CommandManager commandManager) {
        super("help", "вывести справку по доступным командам");
        this.console = console;
        this.commandManager = commandManager;
        setRequiresAuth(false);
    }

    @Override
    public boolean apply(String[] arguments) {
        console.println("Список доступных команд:");
        Map<String, MainCommand> commands = commandManager.getCommands();

        console.println("\nКоманды, доступные всем пользователям:");
        for (MainCommand command : commands.values()) {
            if (!command.requiresAuth()) {
                console.println(String.format("  %s - %s", command.getName(), command.getDescription()));
            }
        }

        console.println("\nКоманды, требующие авторизации:");
        for (MainCommand command : commands.values()) {
            if (command.requiresAuth()) {
                console.println(String.format("  %s - %s", command.getName(), command.getDescription()));
            }
        }

        return true;
    }
}