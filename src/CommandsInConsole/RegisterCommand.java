package CommandsInConsole;

import Console.Console;
import Utilite.UserManager;

public class RegisterCommand extends MainCommand {
    private final Console console;
    private final UserManager userManager;

    public RegisterCommand(Console console, UserManager userManager) {
        super("register", "зарегистрировать нового пользователя");
        this.console = console;
        this.userManager = userManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        if (userManager.isAuthenticated()) {
            console.println("Вы уже вошли в систему. Сначала выйдите.");
            return false;
        }

        console.println("Регистрация нового пользователя");
        console.print("Придумайте имя пользователя: ");
        String username = console.readLine();
        console.print("Придумайте пароль: ");
        String password = console.readLine();
        console.print("Повторите пароль: ");
        String confirmPassword = console.readLine();

        if (!password.equals(confirmPassword)) {
            console.println("Пароли не совпадают");
            return false;
        }

        if (userManager.register(username, password)) {
            console.println("Регистрация успешна. Вы автоматически вошли в систему.");
            return true;
        } else {
            console.println("Ошибка при регистрации. Возможно, такой пользователь уже существует.");
            return false;
        }
    }
} 