import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MaintenanceStatusManager extends JFrame {
    // Éléments UI
    private JTable maintenanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusComboBox;
    private JButton updateButton;
    private JButton refreshButton;
    private JTextField searchField;
    private JButton searchButton;
    private JLabel statusLabel;
    private JButton addButton;
    
    // Constantes
    private static final String[] STATUS_OPTIONS = {"Planifié", "En cours", "Terminé", "Annulé", "Reporté"};
    private static final String[] INTERVENTION_TYPES = {
        "Vidange", "Changement de pneus", "Révision générale", 
        "Réparation moteur", "Contrôle technique", "Autre"
    };
    private static final String DB_URL = "jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb";
    
    // Couleurs et style
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private static final Color SECONDARY_COLOR = new Color(66, 66, 66);
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color BUTTON_ORANGE = new Color(255, 152, 0); // Orange au lieu de vert
    private static final Color WARNING_COLOR = new Color(255, 152, 0);
    
    // Police personnalisée
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    // Variables de connexion
    private Connection connection;
    private int selectedMaintenanceId = -1;
    private String currentUserId;

    public MaintenanceStatusManager(JFrame parent, String userId) {
        this.currentUserId = userId;
        setTitle("Gestion des Statuts de Maintenance");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        initComponents();
        connectToDatabase();
        loadMaintenanceData();
        setVisible(true);
    }

    public static void displayMaintenances(JFrame parent, String userId) {
        new MaintenanceStatusManager(parent, userId);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
        
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        panel.setBackground(PRIMARY_COLOR);
        
        JLabel titleLabel = new JLabel("Gestion des Maintenances");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.BLACK);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(PRIMARY_COLOR);
        
        // Bouton Ajouter orange
        addButton = createStyledButton("+ Ajouter", BUTTON_ORANGE);
        addButton.addActionListener(e -> showAddMaintenanceDialog());
        rightPanel.add(addButton);
        
        // Recherche
        searchField = new JTextField(20);
        searchField.setFont(REGULAR_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        searchButton = createStyledButton("Rechercher", new Color(255, 255, 255, 180));
        searchButton.addActionListener(e -> searchMaintenance());
        
        rightPanel.add(new JLabel("Recherche: "));
        rightPanel.add(searchField);
        rightPanel.add(searchButton);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.setBackground(BACKGROUND_COLOR);
        
        String[] columnNames = {"ID", "Type d'intervention", "Date planifiée", "ID Véhicule", "Description", "ID Utilisateur", "Statut"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        maintenanceTable = new JTable(tableModel);
        maintenanceTable.setFont(REGULAR_FONT);
        maintenanceTable.setRowHeight(30);
        maintenanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        maintenanceTable.setShowGrid(true);
        maintenanceTable.setGridColor(new Color(200, 200, 200));
        
        JTableHeader header = maintenanceTable.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(Color.BLACK);
        
        maintenanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = maintenanceTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedMaintenanceId = (int) maintenanceTable.getValueAt(selectedRow, 0);
                    String currentStatus = (String) maintenanceTable.getValueAt(selectedRow, 6);
                    statusComboBox.setSelectedItem(currentStatus);
                    updateButton.setEnabled(true);
                } else {
                    selectedMaintenanceId = -1;
                    updateButton.setEnabled(false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(maintenanceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        maintenanceTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel statusSelectLabel = new JLabel("Nouveau statut:");
        statusSelectLabel.setFont(REGULAR_FONT);
        
        statusComboBox = new JComboBox<>(STATUS_OPTIONS);
        statusComboBox.setFont(REGULAR_FONT);
        statusComboBox.setPreferredSize(new Dimension(150, 35));
        
        // Bouton Mettre à jour orange
        updateButton = createStyledButton("Mettre à jour", BUTTON_ORANGE);
        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> updateMaintenanceStatus());
        
        refreshButton = createStyledButton("Actualiser", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadMaintenanceData());
        
        controlsPanel.add(statusSelectLabel);
        controlsPanel.add(statusComboBox);
        controlsPanel.add(updateButton);
        controlsPanel.add(refreshButton);
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(REGULAR_FONT);
        
        panel.add(controlsPanel, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void showAddMaintenanceDialog() {
        JDialog dialog = new JDialog(this, "Ajouter une maintenance", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        formPanel.setBackground(BACKGROUND_COLOR);
        
        // Type d'intervention
        JLabel typeLabel = new JLabel("Type d'intervention:");
        typeLabel.setFont(REGULAR_FONT);
        JComboBox<String> typeCombo = new JComboBox<>(INTERVENTION_TYPES);
        typeCombo.setFont(REGULAR_FONT);
        
        // Date planifiée
        JLabel dateLabel = new JLabel("Date planifiée:");
        dateLabel.setFont(REGULAR_FONT);
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        dateField.setFont(REGULAR_FONT);
        
        // ID Véhicule
        JLabel vehicleLabel = new JLabel("ID Véhicule:");
        vehicleLabel.setFont(REGULAR_FONT);
        JTextField vehicleField = new JTextField();
        vehicleField.setFont(REGULAR_FONT);
        
        // Description
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(REGULAR_FONT);
        JTextArea descArea = new JTextArea();
        descArea.setFont(REGULAR_FONT);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        
        formPanel.add(typeLabel);
        formPanel.add(typeCombo);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(vehicleLabel);
        formPanel.add(vehicleField);
        formPanel.add(descLabel);
        formPanel.add(descScroll);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton cancelButton = createStyledButton("Annuler", Color.GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Bouton Enregistrer orange
        JButton saveButton = createStyledButton("Enregistrer", BUTTON_ORANGE);
        saveButton.addActionListener(e -> {
            if (addMaintenance(
                (String) typeCombo.getSelectedItem(),
                dateField.getText(),
                vehicleField.getText(),
                descArea.getText()
            )) {
                dialog.dispose();
                loadMaintenanceData();
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private boolean addMaintenance(String type, String date, String vehicleId, String description) {
        try {
            if (connection == null || connection.isClosed()) {
                connectToDatabase();
            }
            
            String query = "INSERT INTO Maintenance (Type_intervention, Date_planification, id_Vehicule, Description, id_Users, Statut) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, type);
                pstmt.setString(2, date);
                pstmt.setInt(3, Integer.parseInt(vehicleId));
                pstmt.setString(4, description);
                pstmt.setString(5, currentUserId);
                pstmt.setString(6, "Planifié");
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    statusLabel.setText("Maintenance ajoutée avec succès");
                    statusLabel.setForeground(BUTTON_ORANGE);
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "L'ID du véhicule doit être un nombre valide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors de l'ajout: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'ajout de la maintenance:\n" + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 35));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            statusLabel.setText("Connecté à la base de données");
            statusLabel.setForeground(BUTTON_ORANGE);
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur de connexion: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, 
                "Erreur de connexion à la base de données:\n" + e.getMessage(),
                "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadMaintenanceData() {
        tableModel.setRowCount(0);
        
        try {
            if (connection == null || connection.isClosed()) {
                connectToDatabase();
            }
            
            String query = "SELECT id_Maintenance, Type_intervention, Date_planification, " +
                           "id_Vehicule, Description, id_Users, Statut " +
                           "FROM Maintenance ORDER BY Date_planification DESC";
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("id_Maintenance"),
                        rs.getString("Type_intervention"),
                        rs.getDate("Date_planification"),
                        rs.getInt("id_Vehicule"),
                        rs.getString("Description"),
                        rs.getString("id_Users"),
                        rs.getString("Statut")
                    };
                    tableModel.addRow(row);
                }
                
                statusLabel.setText(tableModel.getRowCount() + " maintenances trouvées");
                statusLabel.setForeground(new Color(33, 33, 33));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur de chargement: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des données:\n" + e.getMessage(),
                "Erreur de données", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchMaintenance() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadMaintenanceData();
            return;
        }
        
        try {
            if (connection == null || connection.isClosed()) {
                connectToDatabase();
            }
            
            tableModel.setRowCount(0);
            
            String query = "SELECT id_Maintenance, Type_intervention, Date_planification, " +
                           "id_Vehicule, Description, id_Users, Statut " +
                           "FROM Maintenance " +
                           "WHERE Type_intervention LIKE ? OR Description LIKE ? OR Statut LIKE ? " +
                           "ORDER BY Date_planification DESC";
            
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                String likePattern = "%" + searchTerm + "%";
                pstmt.setString(1, likePattern);
                pstmt.setString(2, likePattern);
                pstmt.setString(3, likePattern);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = {
                            rs.getInt("id_Maintenance"),
                            rs.getString("Type_intervention"),
                            rs.getDate("Date_planification"),
                            rs.getInt("id_Vehicule"),
                            rs.getString("Description"),
                            rs.getString("id_Users"),
                            rs.getString("Statut")
                        };
                        tableModel.addRow(row);
                    }
                    
                    statusLabel.setText(tableModel.getRowCount() + " résultats pour \"" + searchTerm + "\"");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur de recherche: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }
    
    private void updateMaintenanceStatus() {
        if (selectedMaintenanceId == -1) {
            return;
        }
        
        String newStatus = (String) statusComboBox.getSelectedItem();
        
        try {
            if (connection == null || connection.isClosed()) {
                connectToDatabase();
            }
            
            String query = "UPDATE Maintenance SET Statut = ? WHERE id_Maintenance = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, selectedMaintenanceId);
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    int selectedRow = maintenanceTable.getSelectedRow();
                    tableModel.setValueAt(newStatus, selectedRow, 6);
                    
                    statusLabel.setText("Statut mis à jour avec succès");
                    statusLabel.setForeground(BUTTON_ORANGE);
                    
                    animateStatusChange(selectedRow);
                } else {
                    statusLabel.setText("Erreur: Aucune ligne mise à jour");
                    statusLabel.setForeground(Color.RED);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur de mise à jour: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la mise à jour du statut:\n" + e.getMessage(),
                "Erreur de mise à jour", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void animateStatusChange(int row) {
        final Color originalColor = maintenanceTable.getBackground();
        final Color highlightColor = new Color(255, 152, 0, 100); // Orange transparent
        
        new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    SwingUtilities.invokeLater(() -> {
                        maintenanceTable.setSelectionBackground(highlightColor);
                    });
                    Thread.sleep(150);
                    
                    SwingUtilities.invokeLater(() -> {
                        maintenanceTable.setSelectionBackground(originalColor);
                    });
                    Thread.sleep(150);
                }
                
                SwingUtilities.invokeLater(() -> {
                    maintenanceTable.setSelectionBackground(new Color(51, 153, 255));
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            String status = (String) value;
            
            if (!isSelected) {
                switch (status) {
                    case "Planifié":
                        c.setBackground(new Color(33, 150, 243, 50));
                        break;
                    case "En cours":
                        c.setBackground(new Color(255, 152, 0, 50)); // Orange
                        break;
                    case "Terminé":
                        c.setBackground(new Color(76, 175, 80, 50));
                        break;
                    case "Annulé":
                        c.setBackground(new Color(244, 67, 54, 50));
                        break;
                    case "Reporté":
                        c.setBackground(new Color(156, 39, 176, 50));
                        break;
                    default:
                        c.setBackground(table.getBackground());
                }
            }
            
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getFont().deriveFont(Font.BOLD));
            
            return c;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Table.selectionBackground", new Color(51, 153, 255, 100));
            UIManager.put("Table.selectionForeground", Color.BLACK);
            UIManager.put("Table.gridColor", new Color(240, 240, 240));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new MaintenanceStatusManager(null, "Admin"));
    }
}