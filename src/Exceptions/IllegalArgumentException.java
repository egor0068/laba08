package Exceptions;

public class IllegalArgumentException extends Exception{
    private String message;

    @Override
    public String getMessage(){
        return this.message;
    }
}
