package CommandsInConsole;

import Console.Console;
import Utilite.UserManager;

public class LogoutCommand extends MainCommand {
    private final Console console;
    private final UserManager userManager;

    public LogoutCommand(Console console, UserManager userManager) {
        super("logout", "выйти из системы");
        this.console = console;
        this.userManager = userManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        if (!userManager.isAuthenticated()) {
            console.println("Вы не вошли в систему");
            return false;
        }

        userManager.logout();
        console.println("Вы успешно вышли из системы");
        return true;
    }
} 