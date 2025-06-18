package Exceptions;

public class CollectionsEmptyException extends Exception {
    public String getMessage() {
        return "Ошибка! Коллекция пустая.";
    }
}
