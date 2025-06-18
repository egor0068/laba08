package CommandsInConsole;

import Console.Console;

public class ExitCommand extends MainCommand {
    private final Console console;

    public ExitCommand(Console console) {
        super("exit", "завершить программу");
        this.console = console;
    }
    @Override
    public boolean apply(String[] arguments) {
        if (arguments.length > 1 && !arguments[1].isEmpty()) {
            console.println("Использование: " + getName());
            return false;
        }
        console.println("Завершение программы...");
        System.exit(0);
        return true;
    }
}