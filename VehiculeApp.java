
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculeApp {
    // Couleurs du thème
    private static final Color PRIMARY_COLOR = new Color(25, 55, 109);
    private static final Color SECONDARY_COLOR = new Color(45, 95, 155);
    private static final Color ACCENT_COLOR = new Color(240, 130, 50);
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 245);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    
    // Polices
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    
    private String marque, modele, immatriculation, prixJournalier, disponible;

    public VehiculeApp(String marque, String modele, String immatriculation, String prixJournalier, String disponible) {
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.prixJournalier = prixJournalier;
        this.disponible = disponible;
    }

    public String getMarque() { return marque; }
    public String getModele() { return modele; }
    public String getImmatriculation() { return immatriculation; }
    public String getPrixJournalier() { return prixJournalier; }
    public String getDisponible() { return disponible; }

    public static Connection connectToDatabase() throws SQLException {
        String url = "jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb";
        return DriverManager.getConnection(url);
    }

    public static List<VehiculeApp> getAllVehicules() {
        List<VehiculeApp> vehicules = new ArrayList<>();
        try (Connection conn = connectToDatabase();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Vehicule")) {
            
            while (rs.next()) {
                vehicules.add(new VehiculeApp(
                    rs.getString("marque"),
                    rs.getString("modele"),
                    rs.getString("immatriculation"),
                    rs.getString("prix_journalier"),
                    rs.getString("disponible")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors du chargement des véhicules: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return vehicules;
    }

    public static void displayVehiculesWithSearch(MenuPrincipal parent) {
        JFrame frame = new JFrame("Gestion des Véhicules • AyaCar");
        frame.setSize(1000, 650);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        // ==================== HEADER ====================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JLabel titleLabel = new JLabel("GESTION DES VÉHICULES");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.BLACK);
        
        JButton retourBtn = createStyledButton("Retour", Color.BLACK, SECONDARY_COLOR);
        retourBtn.addActionListener(e -> {
            frame.dispose();
            if (parent != null) {
                parent.setVisible(true);
            }
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(retourBtn, BorderLayout.EAST);

        // ==================== TABLEAU ====================
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Marque", "Modèle", "Immatriculation", "Prix Journalier", "Disponible"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }
                return c;
            }
        };
        
        table.setFont(TABLE_FONT);
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(220, 240, 255));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.BLACK);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(210, 210, 210)),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));

        updateVehiculeTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // ==================== RECHERCHE ====================
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setFont(BUTTON_FONT);
        
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setFont(TABLE_FONT);
        
        JButton searchButton = createStyledButton("Rechercher", Color.BLACK, new Color(220, 220, 220));
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            filterTable(model, table, searchTerm);
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // ==================== BOUTONS ====================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton addBtn = createStyledButton("Ajouter", Color.BLACK, ACCENT_COLOR);
        addBtn.addActionListener(e -> showAddVehiculeDialog(frame, model));
        
        JButton editBtn = createStyledButton("Modifier", Color.BLACK, SECONDARY_COLOR);
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                showEditVehiculeDialog(frame, model, table, selectedRow);
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Veuillez sélectionner un véhicule à modifier", 
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton deleteBtn = createStyledButton("Supprimer", Color.BLACK, ERROR_COLOR);
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                deleteVehiculeAction(frame, model, table, selectedRow);
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Veuillez sélectionner un véhicule à supprimer", 
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        for (JButton btn : new JButton[]{addBtn, editBtn, deleteBtn}) {
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 20, 5, 20)
            ));
        }
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        // ==================== ASSEMBLAGE ====================
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void filterTable(DefaultTableModel model, JTable table, String searchTerm) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        if (searchTerm.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm));
        }
    }

    private static void showAddVehiculeDialog(JFrame parent, DefaultTableModel model) {
        JDialog dialog = new JDialog(parent, "Ajouter un véhicule", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel marqueLabel = new JLabel("Marque:");
        JTextField marqueField = new JTextField();
        
        JLabel modeleLabel = new JLabel("Modèle:");
        JTextField modeleField = new JTextField();
        
        JLabel immatLabel = new JLabel("Immatriculation:");
        JTextField immatField = new JTextField();
        
        JLabel prixLabel = new JLabel("Prix Journalier:");
        JTextField prixField = new JTextField();
        
        JLabel dispoLabel = new JLabel("Disponible (Oui/Non):");
        JTextField dispoField = new JTextField();

        formPanel.add(marqueLabel);
        formPanel.add(marqueField);
        formPanel.add(modeleLabel);
        formPanel.add(modeleField);
        formPanel.add(immatLabel);
        formPanel.add(immatField);
        formPanel.add(prixLabel);
        formPanel.add(prixField);
        formPanel.add(dispoLabel);
        formPanel.add(dispoField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton saveBtn = createStyledButton("Enregistrer", Color.BLACK, ACCENT_COLOR);
        JButton cancelBtn = createStyledButton("Annuler", Color.BLACK, new Color(220, 220, 220));
        
        saveBtn.addActionListener(e -> {
            if (validateVehiculeFields(marqueField, modeleField, immatField, prixField, dispoField)) {
                boolean success = addVehicule(
                    marqueField.getText().trim(),
                    modeleField.getText().trim(),
                    prixField.getText().trim(),
                    dispoField.getText().trim(),
                    immatField.getText().trim()
                );
                
                if (success) {
                    dialog.dispose();
                    model.setRowCount(0);
                    updateVehiculeTable(model);
                }
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(PRIMARY_COLOR);
        JLabel titleLabel = new JLabel("AJOUTER UN VÉHICULE");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel);

        dialog.add(titlePanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private static void showEditVehiculeDialog(JFrame parent, DefaultTableModel model, JTable table, int selectedRow) {
        String immatriculation = table.getValueAt(selectedRow, 2).toString();
        
        JDialog dialog = new JDialog(parent, "Modifier un véhicule", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel marqueLabel = new JLabel("Marque:");
        JTextField marqueField = new JTextField(table.getValueAt(selectedRow, 0).toString());
        
        JLabel modeleLabel = new JLabel("Modèle:");
        JTextField modeleField = new JTextField(table.getValueAt(selectedRow, 1).toString());
        
        JLabel prixLabel = new JLabel("Prix Journalier:");
        JTextField prixField = new JTextField(table.getValueAt(selectedRow, 3).toString());
        
        JLabel dispoLabel = new JLabel("Disponible (Oui/Non):");
        JTextField dispoField = new JTextField(table.getValueAt(selectedRow, 4).toString());

        formPanel.add(marqueLabel);
        formPanel.add(marqueField);
        formPanel.add(modeleLabel);
        formPanel.add(modeleField);
        formPanel.add(prixLabel);
        formPanel.add(prixField);
        formPanel.add(dispoLabel);
        formPanel.add(dispoField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton saveBtn = createStyledButton("Enregistrer", Color.BLACK, SECONDARY_COLOR);
        JButton cancelBtn = createStyledButton("Annuler", Color.BLACK, new Color(220, 220, 220));
        
        saveBtn.addActionListener(e -> {
            if (validateVehiculeFields(marqueField, modeleField, null, prixField, dispoField)) {
                boolean success = updateVehicule(
                    immatriculation,
                    marqueField.getText().trim(),
                    modeleField.getText().trim(),
                    prixField.getText().trim(),
                    dispoField.getText().trim()
                );
                
                if (success) {
                    dialog.dispose();
                    model.setRowCount(0);
                    updateVehiculeTable(model);
                }
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(SECONDARY_COLOR);
        JLabel titleLabel = new JLabel("MODIFIER UN VÉHICULE");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel);

        dialog.add(titlePanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private static void deleteVehiculeAction(JFrame parent, DefaultTableModel model, JTable table, int selectedRow) {
        String immatriculation = table.getValueAt(selectedRow, 2).toString();
        int confirm = JOptionPane.showConfirmDialog(
            parent,
            "Êtes-vous sûr de vouloir supprimer ce véhicule?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = deleteVehicule(immatriculation);
            if (success) {
                model.setRowCount(0);
                updateVehiculeTable(model);
            }
        }
    }

    private static boolean validateVehiculeFields(JTextField marque, JTextField modele, 
                                                JTextField immatriculation, JTextField prix, 
                                                JTextField disponible) {
        if (marque.getText().trim().isEmpty() || modele.getText().trim().isEmpty() ||
            (immatriculation != null && immatriculation.getText().trim().isEmpty()) ||
            prix.getText().trim().isEmpty() || disponible.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "Tous les champs sont obligatoires!", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            Double.parseDouble(prix.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, 
                "Le prix journalier doit être un nombre valide!", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!disponible.getText().trim().equalsIgnoreCase("Oui") && 
            !disponible.getText().trim().equalsIgnoreCase("Non")) {
            JOptionPane.showMessageDialog(null, 
                "Disponible doit être 'Oui' ou 'Non'!", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    public static void updateVehiculeTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<VehiculeApp> vehicules = getAllVehicules();
        for (VehiculeApp v : vehicules) {
            model.addRow(new Object[]{
                v.getMarque(), 
                v.getModele(), 
                v.getImmatriculation(), 
                v.getPrixJournalier(), 
                v.getDisponible()
            });
        }
    }

    public static boolean deleteVehicule(String immatriculation) {
        try (Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(
                 "DELETE FROM Vehicule WHERE immatriculation = ?")) {
            
            stmt.setString(1, immatriculation);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, 
                    "Véhicule supprimé avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de la suppression: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public static boolean updateVehicule(String immatriculation, String marque, String modele, 
                                       String prix, String dispo) {
        try (Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE Vehicule SET marque = ?, modele = ?, prix_journalier = ?, disponible = ? " +
                 "WHERE immatriculation = ?")) {
            
            stmt.setString(1, marque);
            stmt.setString(2, modele);
            stmt.setString(3, prix);
            stmt.setString(4, dispo);
            stmt.setString(5, immatriculation);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, 
                    "Véhicule modifié avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de la modification: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public static boolean addVehicule(String marque, String modele, String prix, 
                                    String dispo, String immatriculation) {
        try (Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO Vehicule (marque, modele, prix_journalier, disponible, immatriculation) " +
                 "VALUES (?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, marque);
            stmt.setString(2, modele);
            stmt.setString(3, prix);
            stmt.setString(4, dispo);
            stmt.setString(5, immatriculation);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, 
                    "Véhicule ajouté avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de l'ajout: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private static JButton createStyledButton(String text, Color textColor, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                displayVehiculesWithSearch(null);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Erreur lors de l'initialisation: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}