package com.example;

import Data.LabWork;
import Data.Coordinates;
import Data.Discipline;
import Data.Difficulty;
import Enum.DifficultyEnum;
import Utilite.DatabaseManager;
import Utilite.UserManager;
import com.example.Utilite.LocalizationManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {
    private final UserManager userManager;
    private final DatabaseManager databaseManager;
    private JTable table;
    private DefaultTableModel tableModel;
    private final JLabel userLabel;
    private final JTextField filterField;
    private final JComboBox<String> filterColumnCombo;
    private final JComboBox<String> sortColumnCombo;
    private final JComboBox<String> sortOrderCombo;
    private Map<Integer, LabWork> collection;
    private final String username;
    private final VisualizationPanel visualizationPanel;
    private LocalizationManager localizationManager;
    private JComboBox<Locale> languageSelector;
    private JButton logoutButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton removeButton;
    private JButton clearButton;
    private JButton saveButton;
    private JButton scriptButton;
    private JLabel filterLabel;
    private JLabel sortByLabel;
    private JLabel orderLabel;
    private JButton sortButton;
    private JButton applyFilterButton;
    private JButton infoButton;

    private static final String[] FILTER_KEYS = {
        "id", "name", "coordinates", "creationDate", "minimalPoint", "personalQualities", "difficulty", "discipline"
    };

    public MainWindow(UserManager userManager, DatabaseManager databaseManager, Locale initialLocale) {
        System.out.println("Конструктор MainWindow: начало");
        this.userManager = userManager;
        this.databaseManager = databaseManager;
        this.collection = new TreeMap<>();
        this.username = userManager.getCurrentUser().get().getUsername();
        this.localizationManager = LocalizationManager.getInstance();
        this.localizationManager.setLocale(initialLocale);
        System.out.println("Initial locale: " + localizationManager.getCurrentLocale());

        setTitle("Лабораторная работа 8");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        userLabel = new JLabel("Пользователь: " + username);
        logoutButton = new JButton("Выйти");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(userLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

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

        languageSelector.setSelectedItem(initialLocale);
        languageSelector.addActionListener(e -> {
            Locale selectedLocale = (Locale) languageSelector.getSelectedItem();
            if (selectedLocale != null) {
                localizationManager.setLocale(selectedLocale);
                updateLocalization();
            }
        });
        topPanel.add(languageSelector, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        centerSplitPane.setDividerLocation(400);
        JPanel visualizationContainer = new JPanel(new BorderLayout());

        visualizationPanel = new VisualizationPanel();
        int userId = userManager.getCurrentUser().get().getId();
        visualizationPanel.setCurrentUserId(userId);
        visualizationContainer.add(visualizationPanel, BorderLayout.CENTER);
        centerSplitPane.setTopComponent(visualizationContainer);


        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterLabel = new JLabel(localizationManager.getMessage("filter"));
        filterField = new JTextField(20);
        applyFilterButton = new JButton(localizationManager.getMessage("apply.filter"));
        
        filterColumnCombo = new JComboBox<>(new String[] {
            localizationManager.getMessage("table.id"),
            localizationManager.getMessage("table.name"),
            localizationManager.getMessage("table.coordinates"),
            localizationManager.getMessage("table.creationDate"),
            localizationManager.getMessage("table.minimalPoint"),
            localizationManager.getMessage("table.personalQualities"),
            localizationManager.getMessage("table.difficulty"),
            localizationManager.getMessage("table.discipline")
        });
        
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);
        filterPanel.add(filterColumnCombo);
        filterPanel.add(applyFilterButton);
        applyFilterButton.addActionListener(e -> applyFilter());

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        sortByLabel = new JLabel(localizationManager.getMessage("sort.by"));
        String[] sortFields = {
            localizationManager.getMessage("table.id"),
            localizationManager.getMessage("table.name"),
            localizationManager.getMessage("table.creationDate"),
            localizationManager.getMessage("table.minimalPoint"),
            localizationManager.getMessage("table.personalQualities")
        };
        sortColumnCombo = new JComboBox<>(sortFields);
        orderLabel = new JLabel(localizationManager.getMessage("order"));
        String[] sortOrders = {
            localizationManager.getMessage("ascending"),
            localizationManager.getMessage("descending")
        };
        sortOrderCombo = new JComboBox<>(sortOrders);
        sortButton = new JButton(localizationManager.getMessage("apply.sort"));
        sortButton.addActionListener(e -> applySort());
        sortPanel.add(sortByLabel);
        sortPanel.add(sortColumnCombo);
        sortPanel.add(orderLabel);
        sortPanel.add(sortOrderCombo);
        sortPanel.add(sortButton);

        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.add(filterPanel, BorderLayout.NORTH);
        controlPanel.add(sortPanel, BorderLayout.CENTER);

        String[] columnNames = {"ID", "Название", "Координаты", "Дата создания", "Минимальный балл", "Личные качества", "Сложность", "Дисциплина"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int id = (int) table.getValueAt(selectedRow, 0);
                    LabWork work = collection.values().stream()
                        .filter(w -> w.getID() == id)
                        .findFirst()
                        .orElse(null);
                    if (work != null) {
                        visualizationPanel.setSelectedLabWork(work);
                    }
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(800, 200));
        controlPanel.add(tableScrollPane, BorderLayout.SOUTH);

        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        addButton = new JButton(localizationManager.getMessage("add"));
        updateButton = new JButton(localizationManager.getMessage("update"));
        removeButton = new JButton(localizationManager.getMessage("remove"));
        clearButton = new JButton(localizationManager.getMessage("clear"));
        saveButton = new JButton(localizationManager.getMessage("save"));
        scriptButton = new JButton(localizationManager.getMessage("script"));
        infoButton = new JButton(localizationManager.getMessage("info"));

        addButton.addActionListener(this::handleAdd);
        updateButton.addActionListener(this::handleUpdate);
        removeButton.addActionListener(this::handleRemove);
        clearButton.addActionListener(this::handleClear);
        saveButton.addActionListener(e -> saveCollection());
        scriptButton.addActionListener(e -> {
            if (scriptButton.isEnabled()) {
                executeScript();
            }
        });
        infoButton.addActionListener(e -> showInfoDialog());

        commandPanel.add(addButton);
        commandPanel.add(updateButton);
        commandPanel.add(removeButton);
        commandPanel.add(clearButton);
        commandPanel.add(saveButton);
        commandPanel.add(scriptButton);
        commandPanel.add(infoButton);

        tablePanel.add(controlPanel, BorderLayout.CENTER);
        tablePanel.add(commandPanel, BorderLayout.SOUTH);

        centerSplitPane.setTopComponent(visualizationContainer);
        centerSplitPane.setBottomComponent(tablePanel);

        mainPanel.add(centerSplitPane, BorderLayout.CENTER);

        add(mainPanel);
        System.out.println("Конструктор MainWindow: окно создано");
        setVisible(true);
        System.out.println("Конструктор MainWindow: окно показано");

        loadCollection();

        updateLocalization();
    }

    private void loadCollection() {
        try {
            System.out.println("Загрузка коллекции...");
            collection = databaseManager.loadCollection();
            System.out.println("Коллекция загружена, размер: " + collection.size());
            updateTable();
            visualizationPanel.setCollection(collection);
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке коллекции: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Ошибка при загрузке коллекции: " + e.getMessage(),
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable() {
        System.out.println("Updating table...");
        tableModel.setRowCount(0);
        for (LabWork lab : collection.values()) {
            System.out.println("Adding row for lab work: " + lab.getName());
            String difficultyKey = "difficulty." + lab.getDifficulty().toString().toLowerCase();
            System.out.println("Difficulty key: " + difficultyKey);
            String localizedDifficulty = localizationManager.getMessage(difficultyKey);
            System.out.println("Localized difficulty: " + localizedDifficulty);
            
            Object[] row = {
                lab.getID(),
                lab.getName(),
                String.format("(%d, %d)", lab.getCoordinates().getX(), lab.getCoordinates().getY()),
                lab.getCreationDate(),
                lab.getMinimalPoint(),
                lab.getPersonalQualitiesMinimum(),
                localizedDifficulty,
                lab.getDiscipline().getName(),
                username
            };
            tableModel.addRow(row);
        }
        System.out.println("Table updated with " + tableModel.getRowCount() + " rows");
    }

    private void applyFilter() {
        String filter = filterField.getText().toLowerCase();
        int selectedIndex = filterColumnCombo.getSelectedIndex();
        String filterKey = FILTER_KEYS[selectedIndex];
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        collection.values().stream()
            .filter(work -> {
                if (filter.isEmpty()) return true;
                switch (filterKey) {
                    case "id":
                        return String.valueOf(work.getID()).toLowerCase().contains(filter);
                    case "name":
                        return work.getName().toLowerCase().contains(filter);
                    case "coordinates":
                        return work.getCoordinates().toString().toLowerCase().contains(filter);
                    case "creationDate":
                        return work.getCreationDate().toString().toLowerCase().contains(filter);
                    case "minimalPoint":
                        return work.getMinimalPoint() != null && String.valueOf(work.getMinimalPoint()).toLowerCase().contains(filter);
                    case "personalQualities":
                        return String.valueOf(work.getPersonalQualitiesMinimum()).toLowerCase().contains(filter);
                    case "difficulty":
                        return work.getDifficulty() != null && work.getDifficulty().toString().toLowerCase().contains(filter);
                    case "discipline":
                        return work.getDiscipline() != null && work.getDiscipline().toString().toLowerCase().contains(filter);
                    default:
                        return true;
                }
            })
            .forEach(work -> model.addRow(new Object[]{
                work.getID(),
                work.getName(),
                work.getCoordinates(),
                work.getCreationDate(),
                work.getMinimalPoint(),
                work.getPersonalQualitiesMinimum(),
                work.getDifficulty(),
                work.getDiscipline()
            }));
    }

    private void applySort() {
        String sortField = (String) sortColumnCombo.getSelectedItem();
        String sortOrder = (String) sortOrderCombo.getSelectedItem();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        List<LabWork> sortedWorks = collection.values().stream()
            .sorted((w1, w2) -> {
                int comparison = 0;
                if (sortField.equals(localizationManager.getMessage("table.id"))) {
                        comparison = w1.getID().compareTo(w2.getID());
                } else if (sortField.equals(localizationManager.getMessage("table.name"))) {
                        comparison = w1.getName().compareTo(w2.getName());
                } else if (sortField.equals(localizationManager.getMessage("table.creationDate"))) {
                        comparison = w1.getCreationDate().compareTo(w2.getCreationDate());
                } else if (sortField.equals(localizationManager.getMessage("table.minimalPoint"))) {
                        Double mp1 = w1.getMinimalPoint();
                        Double mp2 = w2.getMinimalPoint();
                        if (mp1 == null && mp2 == null) comparison = 0;
                        else if (mp1 == null) comparison = -1;
                        else if (mp2 == null) comparison = 1;
                        else comparison = mp1.compareTo(mp2);
                } else if (sortField.equals(localizationManager.getMessage("table.personalQualities"))) {
                        comparison = Float.compare(w1.getPersonalQualitiesMinimum(), w2.getPersonalQualitiesMinimum());
                }
                return sortOrder.equals(localizationManager.getMessage("ascending")) ? comparison : -comparison;
            })
            .collect(Collectors.toList());

        sortedWorks.forEach(work -> {
            model.addRow(new Object[]{
                work.getID(),
                work.getName(),
                work.getCoordinates(),
                work.getCreationDate(),
                work.getMinimalPoint(),
                work.getPersonalQualitiesMinimum(),
                work.getDifficulty(),
                work.getDiscipline()
            });
        });
    }

    private void handleAdd(ActionEvent e) {

        JDialog dialog = new JDialog(this, localizationManager.getMessage("add.element"), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(20);
        JTextField xField = new JTextField(20);
        JTextField yField = new JTextField(20);
        JTextField valueField = new JTextField(20);
        JTextField minimalPointField = new JTextField(20);
        JTextField qualitiesField = new JTextField(20);
        JComboBox<DifficultyEnum> difficultyCombo = new JComboBox<>(DifficultyEnum.values());
        JTextField disciplineNameField = new JTextField(20);
        JTextField lectureHoursField = new JTextField(20);

        addFormField(formPanel, gbc, localizationManager.getMessage("name"), nameField, 0);
        addFormField(formPanel, gbc, localizationManager.getMessage("coordinates.x"), xField, 1);
        addFormField(formPanel, gbc, localizationManager.getMessage("coordinates.y"), yField, 2);
        addFormField(formPanel, gbc, localizationManager.getMessage("value"), valueField, 3);
        addFormField(formPanel, gbc, localizationManager.getMessage("minimal.point"), minimalPointField, 4);
        addFormField(formPanel, gbc, localizationManager.getMessage("personal.qualities"), qualitiesField, 5);
        addFormField(formPanel, gbc, localizationManager.getMessage("difficulty"), difficultyCombo, 6);
        addFormField(formPanel, gbc, localizationManager.getMessage("discipline.name"), disciplineNameField, 7);
        addFormField(formPanel, gbc, localizationManager.getMessage("lecture.hours"), lectureHoursField, 8);

        JButton addButton = new JButton(localizationManager.getMessage("add"));
        addButton.addActionListener(ev -> {
            try {

                LabWork work = new LabWork(
                    Double.parseDouble(valueField.getText()),
                    nameField.getText(),
                    new Coordinates(Integer.parseInt(xField.getText()), Long.parseLong(yField.getText())),
                    LocalDateTime.now(),
                    minimalPointField.getText().isEmpty() ? null : Double.parseDouble(minimalPointField.getText()),
                    Float.parseFloat(qualitiesField.getText()),
                    new Difficulty(((DifficultyEnum) difficultyCombo.getSelectedItem()).name()),
                    new Discipline(disciplineNameField.getText(), Integer.parseInt(lectureHoursField.getText()))
                );
                work.setOwnerId(userManager.getCurrentUser().get().getId());

                collection.put(work.getID(), work);

                databaseManager.insertLabWork(work, work.getID(), userManager.getCurrentUser().get().getId());

                updateTable();
                visualizationPanel.setCollection(collection);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    localizationManager.getMessage("error.number.format") + ": " + ex.getMessage(),
                    localizationManager.getMessage("error"),
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    localizationManager.getMessage("error") + ": " + ex.getMessage(),
                    localizationManager.getMessage("error"),
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton(localizationManager.getMessage("cancel"));
        cancelButton.addActionListener(ev -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleUpdate(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                localizationManager.getMessage("select.element"),
                localizationManager.getMessage("warning"),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getValueAt(selectedRow, 0);
        LabWork work = collection.values().stream()
            .filter(w -> w.getID() == id)
            .findFirst()
            .orElse(null);

        if (work == null) {
            JOptionPane.showMessageDialog(this, 
                localizationManager.getMessage("element.not.found"),
                localizationManager.getMessage("error"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!work.getOwnerId().equals(userManager.getCurrentUser().get().getId())) {
            JOptionPane.showMessageDialog(this, 
                localizationManager.getMessage("cannot.modify.other"),
                localizationManager.getMessage("error"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, localizationManager.getMessage("update.element"), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(work.getName(), 20);
        JTextField xField = new JTextField(String.valueOf(work.getCoordinates().getX()), 20);
        JTextField yField = new JTextField(String.valueOf(work.getCoordinates().getY()), 20);
        JTextField valueField = new JTextField(String.valueOf(work.getValue()), 20);
        JTextField minimalPointField = new JTextField(
            work.getMinimalPoint() != null ? String.valueOf(work.getMinimalPoint()) : "", 20);
        JTextField qualitiesField = new JTextField(String.valueOf(work.getPersonalQualitiesMinimum()), 20);
        JComboBox<DifficultyEnum> difficultyCombo = new JComboBox<>(DifficultyEnum.values());
        if (work.getDifficulty() != null) {
            difficultyCombo.setSelectedItem(DifficultyEnum.valueOf(work.getDifficulty().toString()));
        }
        JTextField disciplineNameField = new JTextField(
            work.getDiscipline() != null ? work.getDiscipline().getName() : "", 20);
        JTextField lectureHoursField = new JTextField(
            work.getDiscipline() != null ? String.valueOf(work.getDiscipline().getLectureHours()) : "", 20);

        addFormField(formPanel, gbc, localizationManager.getMessage("name"), nameField, 0);
        addFormField(formPanel, gbc, localizationManager.getMessage("coordinates.x"), xField, 1);
        addFormField(formPanel, gbc, localizationManager.getMessage("coordinates.y"), yField, 2);
        addFormField(formPanel, gbc, localizationManager.getMessage("value"), valueField, 3);
        addFormField(formPanel, gbc, localizationManager.getMessage("minimal.point"), minimalPointField, 4);
        addFormField(formPanel, gbc, localizationManager.getMessage("personal.qualities"), qualitiesField, 5);
        addFormField(formPanel, gbc, localizationManager.getMessage("difficulty"), difficultyCombo, 6);
        addFormField(formPanel, gbc, localizationManager.getMessage("discipline.name"), disciplineNameField, 7);
        addFormField(formPanel, gbc, localizationManager.getMessage("lecture.hours"), lectureHoursField, 8);

        JButton updateButton = new JButton(localizationManager.getMessage("update"));
        updateButton.addActionListener(ev -> {
            try {

                work.setName(nameField.getText());
                work.setCoordinates(new Coordinates(
                    Integer.parseInt(xField.getText()),
                    Long.parseLong(yField.getText())
                ));
                work.setValue(Double.parseDouble(valueField.getText()));
                work.setMinimalPoint(minimalPointField.getText().isEmpty() ? null :
                    Double.parseDouble(minimalPointField.getText()));
                work.setPersonalQualitiesMinimum(Float.parseFloat(qualitiesField.getText()));
                DifficultyEnum difficultyEnum = (DifficultyEnum) difficultyCombo.getSelectedItem();
                work.setDifficulty(new Difficulty(difficultyEnum.name()));
                
                String disciplineName = disciplineNameField.getText();
                int lectureHours = Integer.parseInt(lectureHoursField.getText());
                work.setDiscipline(new Discipline(disciplineName, lectureHours));

                updateTable();
                visualizationPanel.setCollection(collection);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    localizationManager.getMessage("error.number.format") + ": " + ex.getMessage(),
                    localizationManager.getMessage("error"),
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    localizationManager.getMessage("error") + ": " + ex.getMessage(),
                    localizationManager.getMessage("error"),
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton(localizationManager.getMessage("cancel"));
        cancelButton.addActionListener(ev -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleRemove(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                localizationManager.getMessage("select.element"),
                localizationManager.getMessage("warning"),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getValueAt(selectedRow, 0);
        LabWork work = collection.values().stream()
            .filter(w -> w.getID() == id)
            .findFirst()
            .orElse(null);

        if (work == null) {
            JOptionPane.showMessageDialog(this, 
                localizationManager.getMessage("element.not.found"),
                localizationManager.getMessage("error"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!work.getOwnerId().equals(userManager.getCurrentUser().get().getId())) {
            JOptionPane.showMessageDialog(this, 
                localizationManager.getMessage("cannot.delete.other"),
                localizationManager.getMessage("error"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            localizationManager.getMessage("confirm.delete"),
            localizationManager.getMessage("confirm"),
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {

                collection.entrySet().removeIf(entry -> entry.getValue().getID() == id);

                updateTable();
                visualizationPanel.setCollection(collection);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    localizationManager.getMessage("error.delete") + ": " + ex.getMessage(),
                    localizationManager.getMessage("error"),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleClear(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this,
            localizationManager.getMessage("confirm.clear"),
            localizationManager.getMessage("confirm"),
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {

                collection.clear();

                updateTable();
                visualizationPanel.setCollection(collection);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    localizationManager.getMessage("error.clear") + ": " + ex.getMessage(),
                    localizationManager.getMessage("error"),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSave(ActionEvent e) {
        try {
            databaseManager.saveCollection(collection, userManager.getCurrentUser().get().getId());
            JOptionPane.showMessageDialog(this, "Коллекция успешно сохранена",
                "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при сохранении: " + ex.getMessage(),
                "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void logout() {
        userManager.logout();
        dispose();
        new LoginWindow(userManager, databaseManager);
    }

    private void updateLocalization() {

        String[] columnNames = {
            localizationManager.getMessage("table.id"),
            localizationManager.getMessage("table.name"),
            localizationManager.getMessage("table.coordinates"),
            localizationManager.getMessage("table.creationDate"),
            localizationManager.getMessage("table.minimalPoint"),
            localizationManager.getMessage("table.personalQualities"),
            localizationManager.getMessage("table.difficulty"),
            localizationManager.getMessage("table.discipline")
        };
        tableModel.setColumnIdentifiers(columnNames);

        String[] columnOptions = {
            localizationManager.getMessage("table.id"),
            localizationManager.getMessage("table.name"),
            localizationManager.getMessage("table.coordinates"),
            localizationManager.getMessage("table.creationDate"),
            localizationManager.getMessage("table.minimalPoint"),
            localizationManager.getMessage("table.personalQualities"),
            localizationManager.getMessage("table.difficulty"),
            localizationManager.getMessage("table.discipline")
        };
        filterColumnCombo.removeAllItems();
        for (String option : columnOptions) {
            filterColumnCombo.addItem(option);
        }

        addButton.setText(localizationManager.getMessage("add"));
        updateButton.setText(localizationManager.getMessage("update"));
        removeButton.setText(localizationManager.getMessage("remove"));
        clearButton.setText(localizationManager.getMessage("clear"));
        saveButton.setText(localizationManager.getMessage("save"));
        scriptButton.setText(localizationManager.getMessage("script"));
        infoButton.setText(localizationManager.getMessage("info"));
        logoutButton.setText(localizationManager.getMessage("logout"));

        filterLabel.setText(localizationManager.getMessage("filter"));
        applyFilterButton.setText(localizationManager.getMessage("apply.filter"));
        sortByLabel.setText(localizationManager.getMessage("sort.by"));
        orderLabel.setText(localizationManager.getMessage("order"));
        sortButton.setText(localizationManager.getMessage("apply.sort"));

        String[] sortFields = {
            localizationManager.getMessage("table.id"),
            localizationManager.getMessage("table.name"),
            localizationManager.getMessage("table.creationDate"),
            localizationManager.getMessage("table.minimalPoint"),
            localizationManager.getMessage("table.personalQualities")
        };
        sortColumnCombo.removeAllItems();
        for (String field : sortFields) {
            sortColumnCombo.addItem(field);
        }

        String[] sortOrders = {
            localizationManager.getMessage("ascending"),
            localizationManager.getMessage("descending")
        };
        sortOrderCombo.removeAllItems();
        for (String order : sortOrders) {
            sortOrderCombo.addItem(order);
        }

        if (userLabel != null) {
            userLabel.setText(localizationManager.getMessage("user") + ": " + username);
        }

        setTitle(localizationManager.getMessage("app.title"));

        updateTable();
    }

    private void showInfoDialog() {
        StringBuilder info = new StringBuilder();
        info.append(localizationManager.getMessage("collection.type")).append(": ").append(collection.getClass().getName()).append("\n");
        info.append(localizationManager.getMessage("collection.size")).append(": ").append(collection.size()).append("\n");
        JOptionPane.showMessageDialog(this, info.toString(), 
            localizationManager.getMessage("collection.info"), 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveCollection() {
        try {
            databaseManager.saveCollection(collection, userManager.getCurrentUser().get().getId());
            JOptionPane.showMessageDialog(this,
                localizationManager.getMessage("success.save"),
                localizationManager.getMessage("success"),
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                localizationManager.getMessage("error.save") + ": " + ex.getMessage(),
                localizationManager.getMessage("error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeScript() {
        System.out.println("executeScript: начало");
        File scriptFile = new File(System.getProperty("user.dir"), "command.txt");
        System.out.println("Looking for script file at: " + scriptFile.getAbsolutePath());
        
        if (!scriptFile.exists()) {
            System.out.println("executeScript: файл command.txt не найден");
            JOptionPane.showMessageDialog(this,
                "Файл скрипта не найден: " + scriptFile.getAbsolutePath(),
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("executeScript: файл command.txt найден, начинаю чтение");
        try {
            List<String> lines = Files.readAllLines(scriptFile.toPath());
            System.out.println("Прочитано строк: " + lines.size());
            
            int nextId = collection.isEmpty() ? 1 : Collections.max(collection.keySet()) + 1;
            int i = 0;
            while (i < lines.size()) {
                String line = lines.get(i).trim();
                if (line.equals("add")) {
                    try {

                        while (i + 1 < lines.size() && lines.get(i + 1).trim().isEmpty()) {
                            i++;
                        }
                        
                        if (i + 9 < lines.size()) {
                            String name = lines.get(i + 1).trim();
                            int x = Integer.parseInt(lines.get(i + 2).trim());
                            long y = Long.parseLong(lines.get(i + 3).trim());
                            double value = Double.parseDouble(lines.get(i + 4).trim());
                            double minimalPoint = Double.parseDouble(lines.get(i + 5).trim());
                            float personalQualitiesMinimum = Float.parseFloat(lines.get(i + 6).trim());

                            String difficultyStr = lines.get(i + 7).trim().toUpperCase();
                            Difficulty difficulty;
                            try {
                                difficulty = Difficulty.valueOf(difficultyStr);
                            } catch (IllegalArgumentException e) {
                                System.err.println("Неверное значение сложности: " + difficultyStr);
                                System.err.println("Допустимые значения: " + Arrays.toString(DifficultyEnum.values()));
                                throw new IllegalArgumentException("Неверное значение сложности: " + difficultyStr);
                            }
                            
                            String disciplineName = lines.get(i + 8).trim();
                            int lectureHours = Integer.parseInt(lines.get(i + 9).trim());

                Coordinates coordinates = new Coordinates(x, y);
                            Discipline discipline = new Discipline(disciplineName, lectureHours);

                            LabWork work = new LabWork(
                                value,
                    name,
                    coordinates,
                    LocalDateTime.now(),
                    minimalPoint,
                                personalQualitiesMinimum,
                    difficulty,
                    discipline
                );
                            work.setOwnerId(userManager.getCurrentUser().get().getId());

                            work.setID(nextId++);
                            collection.put(work.getID(), work);

                            try {
                                databaseManager.insertLabWork(work, work.getID(), userManager.getCurrentUser().get().getId());
                                System.out.println("Элемент успешно добавлен в базу данных");
                            } catch (Exception ex) {
                                System.err.println("Ошибка при сохранении элемента в базу данных: " + ex.getMessage());
                            }
                            
                            i += 10;
                        } else {
                            System.out.println("Недостаточно данных для создания элемента");
                            break;
                        }
                    } catch (Exception ex) {
                        System.err.println("Ошибка при создании элемента: " + ex.getMessage());
                        JOptionPane.showMessageDialog(this,
                            "Ошибка при создании элемента: " + ex.getMessage(),
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                        i++;
                    }
                } else {
                    i++;
                }
            }

            try {
                databaseManager.saveCollection(collection, userManager.getCurrentUser().get().getId());
                System.out.println("Коллекция сохранена в базу данных");
            } catch (Exception ex) {
                System.err.println("Ошибка при сохранении в базу данных: " + ex.getMessage());
            }
            
            updateTable();
            visualizationPanel.setCollection(collection);
            System.out.println("executeScript: завершено успешно");
        } catch (IOException ex) {
            System.err.println("Ошибка при чтении скрипта: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                "Ошибка при чтении скрипта: " + ex.getMessage(),
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 