package com.example.Utilite;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {
    private static LocalizationManager instance;
    private ResourceBundle messages;
    private Locale currentLocale;

    private LocalizationManager() {
        // По умолчанию используем русский язык
        setLocale(new Locale("ru"));
    }

    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.messages = ResourceBundle.getBundle("messages", locale);
    }

    public String getMessage(String key) {
        try {
            return messages.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public static Locale[] getAvailableLocales() {
        return new Locale[] {
            new Locale("ru"), // Русский
            new Locale("et"), // Эстонский
            new Locale("it"), // Итальянский
            new Locale("es", "PR") // Испанский (Пуэрто-Рико)
        };
    }
} 