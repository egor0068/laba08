package com.example;

import Utilite.DatabaseManager;
import Utilite.UserManager;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Старт приложения");
        SwingUtilities.invokeLater(() -> {
            System.out.println("Создание DatabaseManager");
            DatabaseManager databaseManager = new DatabaseManager();
            System.out.println("Создание UserManager");
            UserManager userManager = new UserManager(databaseManager);
            System.out.println("Открытие окна входа");
            new LoginWindow(userManager, databaseManager);
        });
    }
} 