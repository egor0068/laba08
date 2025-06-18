package Console;

import java.util.Scanner;

public class Console {
    private final Scanner scanner;

    public Console() {
        this.scanner = new Scanner(System.in);
    }

    public void println(String message) {
        System.out.println(message);
    }

    public void print(String message) {
        System.out.print(message);
    }

    public String readLine() {
        return scanner.nextLine();
    }

    public String readLine(String prompt) {
        print(prompt);
        return readLine();
    }
}