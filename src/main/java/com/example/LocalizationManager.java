package com.example;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {
    private static LocalizationManager instance;
    private ResourceBundle messages;
    private Locale currentLocale;

    private LocalizationManager() {

        setLocale(new Locale("ru"));
    }


    public void setLocale(Locale locale) {
        System.out.println("Setting locale to: " + locale);
        this.currentLocale = locale;
        try {

            ResourceBundle baseBundle = ResourceBundle.getBundle("messages", new Locale(""));

            this.messages = ResourceBundle.getBundle("messages", locale);
            System.out.println("Resource bundle loaded successfully");
            System.out.println("Available keys: " + messages.keySet());

            System.out.println("Testing some keys:");
            System.out.println("table.id: " + messages.getString("table.id"));
            System.out.println("table.name: " + messages.getString("table.name"));
            System.out.println("table.coordinates: " + messages.getString("table.coordinates"));
            System.out.println("filter: " + messages.getString("filter"));
        } catch (Exception e) {
            System.err.println("Error loading resource bundle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getMessage(String key) {
        try {
            String message = messages.getString(key);
            System.out.println("Getting message for key '" + key + "': " + message);
            return message;
        } catch (Exception e) {
            System.err.println("Error getting message for key '" + key + "': " + e.getMessage());
            return key;
        }
    }

} 