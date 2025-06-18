package com.example;

import Utilite.DatabaseManager;
import Utilite.UserManager;
import com.example.Utilite.LocalizationManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

public class LoginWindow extends JFrame {
    private final UserManager userManager;
    private final DatabaseManager databaseManager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private boolean loginSuccessful = false;
    private LocalizationManager localizationManager;
    private JComboBox<Locale> languageSelector;

    public LoginWindow(UserManager userManager, DatabaseManager databaseManager) {
        System.out.println("Конструктор LoginWindow: начало");
        this.userManager = userManager;
        this.databaseManager = databaseManager;
        this.localizationManager = LocalizationManager.getInstance();
        initializeUI();
        System.out.println("Конструктор LoginWindow: окно создано");
        setVisible(true);
        System.out.println("Конструктор LoginWindow: окно показано");
    }

    private void initializeUI() {
        setTitle(localizationManager.getMessage("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        languageSelector = new JComboBox<>(LocalizationManager.getAvailableLocales());
        languageSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Locale) {
                    Locale locale = (Locale) value;
                    setText(locale.getDisplayLanguage(locale));
                }
                return this;
            }
        });
        languageSelector.setSelectedItem(localizationManager.getCurrentLocale());
        languageSelector.addActionListener(e -> {
            Locale selectedLocale = (Locale) languageSelector.getSelectedItem();
            if (selectedLocale != null) {
                localizationManager.setLocale(selectedLocale);
                updateLocalization();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(languageSelector, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameLabel = new JLabel(localizationManager.getMessage("username"));
        mainPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        passwordLabel = new JLabel(localizationManager.getMessage("password"));
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginButton = new JButton(localizationManager.getMessage("login"));
        registerButton = new JButton(localizationManager.getMessage("register"));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        mainPanel.add(buttonPanel, gbc);

        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(this::handleRegister);

        add(mainPanel);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!loginSuccessful) {
                    System.exit(0);
                }
            }
        });
    }

    private void updateLocalization() {
        setTitle(localizationManager.getMessage("app.title"));

        usernameLabel.setText(localizationManager.getMessage("username"));
        passwordLabel.setText(localizationManager.getMessage("password"));

        loginButton.setText(localizationManager.getMessage("login"));
        registerButton.setText(localizationManager.getMessage("register"));
    }

    private void handleLogin(ActionEvent e) {
        System.out.println("handleLogin: начало");
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError(localizationManager.getMessage("error.fillAllFields"));
            return;
        }

        if (userManager.login(username, password)) {
            System.out.println("handleLogin: вход успешен, открытие MainWindow");
            loginSuccessful = true;
            dispose();
            new MainWindow(userManager, databaseManager, localizationManager.getCurrentLocale());
        } else {
            System.out.println("handleLogin: ошибка входа");
            showError(localizationManager.getMessage("error.login"));
        }
    }

    private void handleRegister(ActionEvent e) {
        System.out.println("handleRegister: начало");
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError(localizationManager.getMessage("error.fillAllFields"));
            return;
        }

        if (userManager.register(username, password)) {
            System.out.println("handleRegister: регистрация успешна");
            JOptionPane.showMessageDialog(this,
                localizationManager.getMessage("success.registration"),
                localizationManager.getMessage("success"),
                JOptionPane.INFORMATION_MESSAGE);
            loginSuccessful = true;
            dispose();
            new MainWindow(userManager, databaseManager, localizationManager.getCurrentLocale());
        } else {
            System.out.println("handleRegister: ошибка регистрации");
            showError(localizationManager.getMessage("error.registration"));
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            localizationManager.getMessage("error"),
            JOptionPane.ERROR_MESSAGE);
    }

} 