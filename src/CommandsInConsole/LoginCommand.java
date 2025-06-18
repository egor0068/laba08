package CommandsInConsole;

import Collection.CollectionManager;
import Console.Console;
import Utilite.UserManager;

public class LoginCommand extends MainCommand {
    private final UserManager userManager;
    private final CollectionManager collectionManager;
    private final Console console;

    public LoginCommand(UserManager userManager, CollectionManager collectionManager) {
        super("login", "Войти в систему");
        this.userManager = userManager;
        this.collectionManager = collectionManager;
        this.console = new Console();
        setRequiresAuth(false);
    }

    @Override
    public boolean apply(String[] args) {
        String username = console.readLine("Введите имя пользователя: ");
        String password = console.readLine("Введите пароль: ");

        try {
            if (userManager.login(username, password)) {
                console.println("Успешный вход в систему");
                collectionManager.loadUserData();
                return true;
            } else {
                console.println("Неверное имя пользователя или пароль");
                return false;
            }
        } catch (Exception e) {
            console.println("Ошибка при входе в систему: " + e.getMessage());
            return false;
        }
    }
} 