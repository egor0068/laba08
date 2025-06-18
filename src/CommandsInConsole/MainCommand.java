package CommandsInConsole;

import Utilite.Describable;
import Utilite.Executable;
import java.util.Objects;

public abstract class MainCommand implements Executable, Describable {
    private final String name;
    private final String description;
    private boolean requiresAuth = true;

    public MainCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setRequiresAuth(boolean requiresAuth) {
        this.requiresAuth = requiresAuth;
    }

    public boolean requiresAuth() {
        return requiresAuth;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}