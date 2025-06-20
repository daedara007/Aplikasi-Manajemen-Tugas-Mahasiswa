package view;

import controller.TaskController;
import controller.CourseController;
import model.User;
import model.Course;
import model.AcademicTask;
import model.PersonalTask;
import model.abstractes.TaskBase;
import model.enums.Status;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import utils.CustomDialog;

public class TaskManagementPanel extends BasePanel {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField deadlineField;
    private JComboBox<String> taskTypeCombo;
    private JComboBox<Course> courseCombo;
    private JTextField categoryField;
    private JButton saveButton;
    private JButton updateStatusButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JLabel dynamicLabel;
    private JPanel dynamicFieldPanel;
    
    // Search and filter components
    private JTextField searchField;
    private JButton searchButton;
    private JButton filterButton;
    private JPanel filterPanel;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> courseFilterCombo;
    private JComboBox<String> deadlineFilterCombo;
    private boolean isFilterPanelVisible = false;
    
    // List to store all tasks for filtering
    private List<TaskBase> allTasks;
    
    // Konstanta untuk ukuran field
    private final Dimension FIELD_SIZE = new Dimension(200, 30);
    private final Dimension SEARCH_FIELD_SIZE = new Dimension(250, 30);
    
    public TaskManagementPanel(MainFrame mainFrame, User user) {
        super(mainFrame, user);
        initTaskComponents();
        setupTaskLayout();
        loadCourses();
        loadAllTasks();
    }
    
    private void initTaskComponents() {
        // Form fields dengan ukuran yang konsisten
        titleField = createStyledTextField();
        titleField.setPreferredSize(FIELD_SIZE);
        
        descriptionArea = new JTextArea(2, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        deadlineField = createStyledTextField();
        deadlineField.setPreferredSize(FIELD_SIZE);
        deadlineField.setText(LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        taskTypeCombo = new JComboBox<>(new String[]{"Academic", "Personal"});
        taskTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        taskTypeCombo.setPreferredSize(FIELD_SIZE);
        
        // Course combo untuk Academic tasks
        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        courseCombo.setPreferredSize(FIELD_SIZE);
        
        // Category field untuk Personal tasks
        categoryField = createStyledTextField();
        categoryField.setPreferredSize(FIELD_SIZE);
        
        // Dynamic label yang berubah sesuai tipe task
        dynamicLabel = createLabel("Matakuliah :");
        
        // Panel dinamis untuk course/category
        dynamicFieldPanel = new JPanel(new CardLayout());
        dynamicFieldPanel.setOpaque(false);
        dynamicFieldPanel.setPreferredSize(FIELD_SIZE);
        
        // Panel untuk course (Academic)
        JPanel coursePanel = new JPanel(new BorderLayout());
        coursePanel.setOpaque(false);
        coursePanel.add(courseCombo, BorderLayout.CENTER);
        
        // Panel untuk category (Personal)
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setOpaque(false);
        categoryPanel.add(categoryField, BorderLayout.CENTER);
        
        dynamicFieldPanel.add(coursePanel, "ACADEMIC");
        dynamicFieldPanel.add(categoryPanel, "PERSONAL");
        
        // Action buttons dengan styling purple
        saveButton = createActionButton("Simpan", BUTTON_COLOR);
        updateStatusButton = createActionButton("Ubah Status", BUTTON_COLOR);
        deleteButton = createActionButton("Hapus", BUTTON_COLOR);
        clearButton = createActionButton("Reset", BUTTON_COLOR.brighter());
        
        // Initialize allTasks list
        allTasks = new ArrayList<>();
        
        // Initialize table FIRST before search filter components
        initializeTable();
        
        // Initialize search and filter components AFTER table
        initSearchFilterComponents();
    }
    
    private void initSearchFilterComponents() {
        // Search field
        searchField = createStyledTextField();
        searchField.setPreferredSize(new Dimension(250, 30));
        searchField.setToolTipText("Cari berdasarkan judul atau deskripsi");
        
        // Search button with custom drawn icon
        searchButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background
                if (getModel().isPressed()) {
                    g2.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(BUTTON_COLOR.brighter());
                } else {
                    g2.setColor(BUTTON_COLOR);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw search icon (magnifying glass)
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Circle part of magnifying glass
                g2.drawOval(centerX - 6, centerY - 6, 8, 8);
                // Handle part
                g2.drawLine(centerX + 1, centerY + 1, centerX + 4, centerY + 4);
                
                g2.dispose();
            }
        };
        searchButton.setPreferredSize(new Dimension(30, 30));
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Filter button with custom drawn icon
        filterButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background
                Color bgColor = isFilterPanelVisible ? BUTTON_COLOR.darker() : BUTTON_COLOR;
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw filter icon (funnel shape)
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Top line
                g2.drawLine(centerX - 6, centerY - 4, centerX + 6, centerY - 4);
                // Left diagonal
                g2.drawLine(centerX - 6, centerY - 4, centerX - 2, centerY);
                // Right diagonal
                g2.drawLine(centerX + 6, centerY - 4, centerX + 2, centerY);
                // Bottom line
                g2.drawLine(centerX - 2, centerY, centerX + 2, centerY);
                // Vertical line
                g2.drawLine(centerX, centerY, centerX, centerY + 4);
                
                g2.dispose();
            }
        };
        filterButton.setPreferredSize(new Dimension(30, 30));
        filterButton.setFocusPainted(false);
        filterButton.setBorderPainted(false);
        filterButton.setContentAreaFilled(false);
        filterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Filter panel
        filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(new Color(255, 200, 255));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Status filter
        JLabel statusLabel = new JLabel("Status");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusFilterCombo = new JComboBox<>(new String[]{"Semua", "Belum Mulai", "Sedang Dikerjakan", "Selesai"});
        statusFilterCombo.setPreferredSize(new Dimension(150, 25));
        
        // Course/Category filter
        JLabel courseLabel = new JLabel("Mata Kuliah");
        courseLabel.setFont(new Font("Arial", Font.BOLD, 12));
        courseFilterCombo = new JComboBox<>();
        courseFilterCombo.setPreferredSize(new Dimension(150, 25));
        
        // Deadline filter
        JLabel deadlineLabel = new JLabel("Tenggat Waktu");
        deadlineLabel.setFont(new Font("Arial", Font.BOLD, 12));
        deadlineFilterCombo = new JComboBox<>(new String[]{"Semua", "1 Hari", "3 Hari", "1 Minggu", "1 Bulan"});
        deadlineFilterCombo.setPreferredSize(new Dimension(150, 25));
        
        // Add components to filter panel
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilterCombo);
        filterPanel.add(courseLabel);
        filterPanel.add(courseFilterCombo);
        filterPanel.add(deadlineLabel);
        filterPanel.add(deadlineFilterCombo);
        
        // Initially hide filter panel
        filterPanel.setVisible(false);
        
        // Add action listeners AFTER all components are created
        searchButton.addActionListener(e -> applyFilters());
        filterButton.addActionListener(e -> toggleFilterPanel());
        
        // Add real-time search listener AFTER table is initialized
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { 
                if (tableModel != null) applyFilters(); 
            }
            @Override
            public void removeUpdate(DocumentEvent e) { 
                if (tableModel != null) applyFilters(); 
            }
            @Override
            public void changedUpdate(DocumentEvent e) { 
                if (tableModel != null) applyFilters(); 
            }
        });
        
        // Add filter combo listeners AFTER table is initialized
        statusFilterCombo.addActionListener(e -> {
            if (tableModel != null) applyFilters();
        });
        courseFilterCombo.addActionListener(e -> {
            if (tableModel != null) applyFilters();
        });
        deadlineFilterCombo.addActionListener(e -> {
            if (tableModel != null) applyFilters();
        });
    }
    
    private void toggleFilterPanel() {
        isFilterPanelVisible = !isFilterPanelVisible;
        filterPanel.setVisible(isFilterPanelVisible);
        filterButton.repaint(); // Repaint to show active state
        revalidate();
        repaint();
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(text, textX, textY);
                
                g2.dispose();
            }
        };
        
        button.setText(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(120, 25));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void initializeTable() {
        String[] columnNames = {"ID", "Judul", "Deskripsi", "Tenggat Waktu", "Mata Kuliah", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        taskTable = new JTable(tableModel);
        taskTable.setFont(new Font("Arial", Font.PLAIN, 12));
        taskTable.setRowHeight(25);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setGridColor(new Color(200, 200, 200));
        
        // Hide ID column
        taskTable.getColumnModel().getColumn(0).setMinWidth(0);
        taskTable.getColumnModel().getColumn(0).setMaxWidth(0);
        taskTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Judul
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Deskripsi
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Deadline
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Mata Kuliah
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        
        // Custom header renderer
        taskTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(CARD_COLOR);
                c.setForeground(TEXT_COLOR);
                c.setFont(new Font("Arial", Font.BOLD, 12));
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        // Add sorting capability
        taskTable.setAutoCreateRowSorter(true);
         
    }
     
    private void setupTaskLayout() {
        // Header panel
        JPanel headerPanel = createHeaderPanel("Tambah Tugas");
        
        // Main content panel dengan background card
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(CARD_COLOR);
                g2.fillRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 20, 20);
                
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 20, 30));
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Search and filter panel
        JPanel searchFilterPanel = createSearchFilterPanel();
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        // Combine panels
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(searchFilterPanel, BorderLayout.CENTER);
        
        // Gunakan BorderLayout untuk memastikan tabel terlihat
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setOpaque(false);
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Bottom navigation panel
        JPanel navPanel = createNavigationPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(navPanel, BorderLayout.SOUTH);
        
        // Setup event handlers
        setupTaskEventHandlers();
    }
    
    private JPanel createSearchFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Search bar panel
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        searchBarPanel.setOpaque(false);
        searchBarPanel.add(searchField);
        searchBarPanel.add(searchButton);
        searchBarPanel.add(filterButton);
        
        panel.add(searchBarPanel, BorderLayout.NORTH);
        panel.add(filterPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Jenis Tugas dan Judul
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Jenis Tugas :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(taskTypeCombo, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Judul :"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(titleField, gbc);
        
        // Row 2: Deadline dan Matakuliah/Kategori
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Tenggat Waktu :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(deadlineField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(dynamicLabel, gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(dynamicFieldPanel, gbc);
        
        // Row 3: Deskripsi (span across all columns)
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Deskripsi :"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.5;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        
        descScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Make scrollbar invisible but keep functionality
        descScrollPane.setPreferredSize(new Dimension(400, 50));
        formPanel.add(descScrollPane, gbc);
        
        // Row 4: Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);  

        // Make scrollbar invisible but keep functionality
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
         
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private void loadCourses() {
        courseCombo.removeAllItems();
        List<Course> courses = CourseController.getCoursesByUserId(currentUser.getUserId());
        
        if (courses.isEmpty()) {
            courseCombo.addItem(new Course("Tidak ada mata kuliah", 0));
        } else {
            for (Course course : courses) {
                courseCombo.addItem(course);
            }
        }
        
        // Also update course filter combo
        updateCourseFilterCombo();
    }
    
    private void updateCourseFilterCombo() {
        courseFilterCombo.removeAllItems();
        courseFilterCombo.addItem("Semua");
        
        // Add courses
        List<Course> courses = CourseController.getCoursesByUserId(currentUser.getUserId());
        for (Course course : courses) {
            courseFilterCombo.addItem(course.getCourseName());
        }
        
        // Add personal categories
        List<String> categories = getUniquePersonalCategories();
        for (String category : categories) {
            courseFilterCombo.addItem(category);
        }
    }
    
    private List<String> getUniquePersonalCategories() {
        List<PersonalTask> personalTasks = TaskController.getPersonalTasksByUserId(currentUser.getUserId());
        return personalTasks.stream()
            .map(PersonalTask::getCategory)
            .distinct()
            .collect(Collectors.toList());
    }
    
    private void loadAllTasks() {
        // Get all tasks from database and store them
        allTasks = TaskController.getAllTasksByUserId(currentUser.getUserId());
        
        // Apply current filters
        applyFilters();
    }
    
    private void applyFilters() {
        // Add null check to prevent errors during initialization
        if (tableModel == null || allTasks == null) {
            return;
        }
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Get filter values
        String searchText = searchField.getText().toLowerCase().trim();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        String courseFilter = (String) courseFilterCombo.getSelectedItem();
        String deadlineFilter = (String) deadlineFilterCombo.getSelectedItem();
        
        // Create predicates for each filter
        Predicate<TaskBase> searchPredicate = task -> 
            searchText.isEmpty() || 
            task.getTitle().toLowerCase().contains(searchText) || 
            task.getDescription().toLowerCase().contains(searchText);
        
        Predicate<TaskBase> statusPredicate = task -> 
            "Semua".equals(statusFilter) || 
            task.getStatus().getDisplayName().equals(statusFilter);
        
        Predicate<TaskBase> coursePredicate = task -> {
            if ("Semua".equals(courseFilter)) {
                return true;
            }
            
            if (task instanceof AcademicTask) {
                String courseName = TaskController.getCourseName(((AcademicTask) task).getCourseId());
                return courseName.equals(courseFilter);
            } else if (task instanceof PersonalTask) {
                String category = ((PersonalTask) task).getCategory();
                return category.equals(courseFilter);
            }
            return false;
        };
        
        Predicate<TaskBase> deadlinePredicate = task -> {
            if ("Semua".equals(deadlineFilter)) {
                return true;
            }
            
            LocalDate today = LocalDate.now();
            LocalDate deadline = task.getDeadline();
            
            return switch (deadlineFilter) {
                case "1 Hari" -> deadline.isEqual(today) || deadline.isEqual(today.plusDays(1));
                case "3 Hari" -> deadline.isAfter(today.minusDays(1)) && deadline.isBefore(today.plusDays(4));
                case "1 Minggu" -> deadline.isAfter(today.minusDays(1)) && deadline.isBefore(today.plusDays(8));
                case "1 Bulan" -> deadline.isAfter(today.minusDays(1)) && deadline.isBefore(today.plusMonths(1).plusDays(1));
                default -> true;
            };
        };
        
        // Combine all predicates
        Predicate<TaskBase> combinedPredicate = searchPredicate
            .and(statusPredicate)
            .and(coursePredicate)
            .and(deadlinePredicate);
        
        // Filter tasks
        List<TaskBase> filteredTasks = allTasks.stream()
            .filter(combinedPredicate)
            .collect(Collectors.toList());
        
        // Add filtered tasks to table
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (TaskBase task : filteredTasks) {
            String courseOrCategory = "";
            
            if (task instanceof AcademicTask) {
                courseOrCategory = TaskController.getCourseName(((AcademicTask) task).getCourseId());
            } else if (task instanceof PersonalTask) {
                courseOrCategory = ((PersonalTask) task).getCategory();
            }
            
            Object[] row = {
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline().format(formatter),
                courseOrCategory,
                task.getStatus().getDisplayName()
            };
            tableModel.addRow(row);
        }
    }
    
    private void setupTaskEventHandlers() {
        saveButton.addActionListener(this::handleSaveTask);
        updateStatusButton.addActionListener(this::handleUpdateStatus);
        deleteButton.addActionListener(this::handleDeleteTask);
        clearButton.addActionListener(this::handleClearForm);
        
        // Task type change handler
        taskTypeCombo.addActionListener(e -> {
            String selectedType = (String) taskTypeCombo.getSelectedItem();
            boolean isAcademic = "Academic".equals(selectedType);
            
            CardLayout cl = (CardLayout) dynamicFieldPanel.getLayout();
            if (isAcademic) {
                dynamicLabel.setText("Matakuliah :");
                cl.show(dynamicFieldPanel, "ACADEMIC");
            } else {
                dynamicLabel.setText("Kategori :");
                cl.show(dynamicFieldPanel, "PERSONAL");
            }
            
            revalidate();
            repaint();
        });
    }
    
    private void handleSaveTask(ActionEvent e) {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String deadlineStr = deadlineField.getText().trim();
        String taskType = (String) taskTypeCombo.getSelectedItem();
        
        if (title.isEmpty() || deadlineStr.isEmpty()) {
            CustomDialog.showError(this, "Judul dan deadline tidak boleh kosong!");
            return;
        }
        
        LocalDate deadline;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            deadline = LocalDate.parse(deadlineStr, formatter);
        } catch (DateTimeParseException ex) {
            CustomDialog.showError(this, "Format tanggal tidak valid! Gunakan dd/MM/yyyy");
            return;
        }
        
        boolean success = false;
        
        if ("Academic".equals(taskType)) {
            Course selectedCourse = (Course) courseCombo.getSelectedItem();
            if (selectedCourse == null || selectedCourse.getCourseId() == 0) {
                CustomDialog.showError(this, "Pilih mata kuliah terlebih dahulu!");
                return;
            }
            
            AcademicTask task = new AcademicTask(0, title, description, deadline, Status.BELUM_MULAI, selectedCourse.getCourseId());
            success = TaskController.addAcademicTask(task);
        } else {
            String category = categoryField.getText().trim();
            if (category.isEmpty()) {
                CustomDialog.showError(this, "Kategori tidak boleh kosong untuk Personal Task!");
                return;
            }
            
            PersonalTask task = new PersonalTask(0, title, description, deadline, Status.BELUM_MULAI, category, currentUser.getUserId());
            success = TaskController.addPersonalTask(task);
        }
        
        if (success) {
            CustomDialog.showSuccess(this, "Tugas berhasil ditambahkan!");
            handleClearForm(null);
            loadAllTasks();
            mainFrame.refreshAllPanels();
        } else {
            CustomDialog.showError(this, "Gagal menambahkan tugas!");
        }
    }
    
    private void handleUpdateStatus(ActionEvent e) {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            CustomDialog.showError(this, "Pilih tugas yang akan diupdate!");
            return;
        }
        
        // Convert view index to model index (important when table is sorted)
        selectedRow = taskTable.convertRowIndexToModel(selectedRow);
        
        String[] statusOptions = {"Belum Mulai", "Sedang Dikerjakan", "Selesai"};
        
        // Use custom status dialog instead of JOptionPane
        String selectedStatus = CustomDialog.showStatusDialog(this, "Pilih status baru untuk tugas:", statusOptions);
        
        if (selectedStatus != null) {
            Status newStatus = Status.BELUM_MULAI;
            switch (selectedStatus) {
                case "Sedang Dikerjakan" -> newStatus = Status.SEDANG_DIKERJAKAN;
                case "Selesai" -> newStatus = Status.SELESAI;
            }
            
            int taskId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            // Get task from database to determine type
            AcademicTask academicTask = TaskController.getAcademicTaskById(taskId);
            PersonalTask personalTask = null;
            if (academicTask == null) {
                personalTask = TaskController.getPersonalTaskById(taskId);
            }
            
            boolean success = false;
            if (academicTask != null) {
                success = TaskController.updateTaskStatus(academicTask, newStatus);
            } else if (personalTask != null) {
                success = TaskController.updateTaskStatus(personalTask, newStatus);
            }
            
            if (success) {
                CustomDialog.showSuccess(this, "Status berhasil diupdate!");
                loadAllTasks();
                mainFrame.refreshAllPanels();
            } else {
                CustomDialog.showError(this, "Gagal mengupdate status!");
            }
        }
    }
    
    private void handleDeleteTask(ActionEvent e) {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            CustomDialog.showError(this, "Pilih tugas yang akan dihapus!");
            return;
        }
        
        // Convert view index to model index (important when table is sorted)
        selectedRow = taskTable.convertRowIndexToModel(selectedRow);
        
        boolean confirm = CustomDialog.showConfirm(this, "Yakin ingin menghapus tugas ini?");
        if (confirm) {
            int taskId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            // Try to delete as academic task first, then personal task
            boolean success = TaskController.deleteAcademicTask(taskId);
            if (!success) {
                success = TaskController.deletePersonalTask(taskId);
            }
            
            if (success) {
                CustomDialog.showSuccess(this, "Tugas berhasil dihapus!");
                loadAllTasks();
                mainFrame.refreshAllPanels();
            } else {
                CustomDialog.showError(this, "Gagal menghapus tugas!");
            }
        }
    }
    
    private void handleClearForm(ActionEvent e) {
        titleField.setText("");
        descriptionArea.setText("");
        deadlineField.setText(LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        taskTypeCombo.setSelectedIndex(0);
        categoryField.setText("");
        
        if (courseCombo.getItemCount() > 0) {
            courseCombo.setSelectedIndex(0);
        }
        
        // Reset ke Academic view
        CardLayout cl = (CardLayout) dynamicFieldPanel.getLayout();
        cl.show(dynamicFieldPanel, "ACADEMIC");
        dynamicLabel.setText("Matakuliah :");
        
        titleField.requestFocus();
    }
    
    // Override BasePanel abstract methods
    @Override
    protected void handleAdd(ActionEvent e) {
        titleField.requestFocus();
    }
    
    @Override
    public void refreshData() {
        loadCourses();
        loadAllTasks();
    }
    
    // Override navigation to highlight current page
    @Override
    protected void handleTask(ActionEvent e) {
        refreshData();
    }
}
