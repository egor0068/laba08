package Utilite;

import CommandsInConsole.MainCommand;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class CommandManager {
    private final Map<String, MainCommand> commands;

    public CommandManager() {
        this.commands = new HashMap<>();
    }

    public void registerCommand(MainCommand command) {
        commands.put(command.getName(), command);
    }

    public MainCommand getCommand(String name) {
        return commands.get(name);
    }

    public Collection<MainCommand> getCommands() {
        return commands.values();
    }
} 