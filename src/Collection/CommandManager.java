package Collection;

import CommandsInConsole.MainCommand;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, MainCommand> commands;

    public CommandManager() {
        this.commands = new HashMap<>();
    }

    public void registerCommand(MainCommand command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    public MainCommand getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public Map<String, MainCommand> getCommands() {
        return commands;
    }
}