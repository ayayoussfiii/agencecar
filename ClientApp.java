import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ClientApp {
    // Couleurs du thème
    private static final Color PRIMARY_COLOR = new Color(25, 55, 109);
    private static final Color SECONDARY_COLOR = new Color(45, 95, 155);
    private static final Color ACCENT_COLOR = new Color(240, 130, 50);
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 245);
    private static final Color TEXT_DARK = new Color(50, 50, 50);
    private static final Color TABLE_HEADER_BG = new Color(230, 235, 240);

    // Polices
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI Semibold", Font.BOLD, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private static Connection connection;

    public static void displayClientsWithSearch() {
        JFrame frame = new JFrame("Gestion des Clients • AyaCar");
        frame.setSize(1000, 650);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        // ==================== HEADER ====================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JLabel titleLabel = new JLabel("GESTION DES CLIENTS");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        JButton retourBtn = createStyledButton("Retour", TEXT_DARK, new Color(245, 245, 245));
        retourBtn.addActionListener(e -> {
            frame.dispose();
            new MenuPrincipal().setVisible(true);
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(retourBtn, BorderLayout.EAST);

        // ==================== TABLEAU ====================
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Prénom", "Téléphone", "Numéro de Permis"}, 0) {
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
        
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setSelectionForeground(TEXT_DARK);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_DARK);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(210, 210, 210)),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));

        loadClientsFromDatabase(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // ==================== RECHERCHE ====================
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setFont(LABEL_FONT);
        
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setFont(INPUT_FONT);
        
        JButton searchButton = createStyledButton("Rechercher", TEXT_DARK, new Color(220, 220, 220));
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (searchTerm.isEmpty()) {
                model.setRowCount(0);
                loadClientsFromDatabase(model);
            } else {
                searchClients(model, searchTerm);
            }
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // ==================== BOUTONS ====================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton addBtn = createStyledButton("Ajouter", TEXT_DARK, ACCENT_COLOR);
        addBtn.addActionListener(e -> showAddClientDialog(frame, model));
        
        JButton editBtn = createStyledButton("Modifier", TEXT_DARK, SECONDARY_COLOR);
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int clientId = (int) model.getValueAt(selectedRow, 0);
                String nom = (String) model.getValueAt(selectedRow, 1);
                String prenom = (String) model.getValueAt(selectedRow, 2);
                String telephone = (String) model.getValueAt(selectedRow, 3);
                String numPermis = (String) model.getValueAt(selectedRow, 4);
                
                showEditClientDialog(frame, model, clientId, nom, prenom, telephone, numPermis);
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Veuillez sélectionner un client à modifier", 
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton deleteBtn = createStyledButton("Supprimer", TEXT_DARK, new Color(255, 100, 100));
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int clientId = (int) model.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Êtes-vous sûr de vouloir supprimer ce client?",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteClient(clientId);
                    model.setRowCount(0);
                    loadClientsFromDatabase(model);
                }
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Veuillez sélectionner un client à supprimer", 
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

    private static void showAddClientDialog(JFrame parent, DefaultTableModel model) {
        JDialog dialog = new JDialog(parent, "Ajouter un client", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel nomLabel = new JLabel("Nom:");
        JTextField nomField = new JTextField(20);
        nomLabel.setFont(LABEL_FONT);
        nomField.setFont(INPUT_FONT);
        
        JLabel prenomLabel = new JLabel("Prénom:");
        JTextField prenomField = new JTextField(20);
        prenomLabel.setFont(LABEL_FONT);
        prenomField.setFont(INPUT_FONT);
        
        JLabel telephoneLabel = new JLabel("Téléphone:");
        JTextField telephoneField = new JTextField(20);
        telephoneLabel.setFont(LABEL_FONT);
        telephoneField.setFont(INPUT_FONT);
        
        JLabel permisLabel = new JLabel("Numéro de Permis:");
        JTextField permisField = new JTextField(20);
        permisLabel.setFont(LABEL_FONT);
        permisField.setFont(INPUT_FONT);

        formPanel.add(nomLabel);
        formPanel.add(nomField);
        formPanel.add(prenomLabel);
        formPanel.add(prenomField);
        formPanel.add(telephoneLabel);
        formPanel.add(telephoneField);
        formPanel.add(permisLabel);
        formPanel.add(permisField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton saveBtn = createStyledButton("Enregistrer", Color.BLACK, ACCENT_COLOR);
        JButton cancelBtn = createStyledButton("Annuler", TEXT_DARK, new Color(220, 220, 220));
        
        saveBtn.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() ||
                telephoneField.getText().trim().isEmpty() || permisField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Tous les champs sont obligatoires!", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = addClient(
                nomField.getText().trim(), 
                prenomField.getText().trim(), 
                telephoneField.getText().trim(), 
                permisField.getText().trim()
            );
            
            if (success) {
                dialog.dispose();
                model.setRowCount(0);
                loadClientsFromDatabase(model);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(PRIMARY_COLOR);
        JLabel titleLabel = new JLabel("AJOUTER UN CLIENT");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        dialog.add(titlePanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private static void showEditClientDialog(JFrame parent, DefaultTableModel model, 
                                            int clientId, String nom, String prenom, 
                                            String telephone, String numPermis) {
        JDialog dialog = new JDialog(parent, "Modifier un client", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel nomLabel = new JLabel("Nom:");
        JTextField nomField = new JTextField(nom, 20);
        nomLabel.setFont(LABEL_FONT);
        nomField.setFont(INPUT_FONT);
        
        JLabel prenomLabel = new JLabel("Prénom:");
        JTextField prenomField = new JTextField(prenom, 20);
        prenomLabel.setFont(LABEL_FONT);
        prenomField.setFont(INPUT_FONT);
        
        JLabel telephoneLabel = new JLabel("Téléphone:");
        JTextField telephoneField = new JTextField(telephone, 20);
        telephoneLabel.setFont(LABEL_FONT);
        telephoneField.setFont(INPUT_FONT);
        
        JLabel permisLabel = new JLabel("Numéro de Permis:");
        JTextField permisField = new JTextField(numPermis, 20);
        permisLabel.setFont(LABEL_FONT);
        permisField.setFont(INPUT_FONT);

        formPanel.add(nomLabel);
        formPanel.add(nomField);
        formPanel.add(prenomLabel);
        formPanel.add(prenomField);
        formPanel.add(telephoneLabel);
        formPanel.add(telephoneField);
        formPanel.add(permisLabel);
        formPanel.add(permisField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton saveBtn = createStyledButton("Enregistrer", Color.BLACK, SECONDARY_COLOR);
        JButton cancelBtn = createStyledButton("Annuler", TEXT_DARK, new Color(220, 220, 220));
        
        saveBtn.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() ||
                telephoneField.getText().trim().isEmpty() || permisField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Tous les champs sont obligatoires!", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = updateClient(
                clientId,
                nomField.getText().trim(), 
                prenomField.getText().trim(), 
                telephoneField.getText().trim(), 
                permisField.getText().trim()
            );
            
            if (success) {
                dialog.dispose();
                model.setRowCount(0);
                loadClientsFromDatabase(model);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(SECONDARY_COLOR);
        JLabel titleLabel = new JLabel("MODIFIER UN CLIENT");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        dialog.add(titlePanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private static void loadClientsFromDatabase(DefaultTableModel model) {
        try (Connection conn = connectToDatabase();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nom, prenom, telephone, num_permis FROM Client")) {
            
            model.setRowCount(0);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("telephone"),
                    rs.getString("num_permis")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors du chargement des clients: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void searchClients(DefaultTableModel model, String searchTerm) {
        try (Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT id, nom, prenom, telephone, num_permis FROM Client " +
                 "WHERE nom LIKE ? OR prenom LIKE ? OR telephone LIKE ? OR num_permis LIKE ?")) {
            
            String term = "%" + searchTerm + "%";
            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setString(3, term);
            stmt.setString(4, term);
            
            ResultSet rs = stmt.executeQuery();
            model.setRowCount(0);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("telephone"),
                    rs.getString("num_permis")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de la recherche: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static boolean addClient(String nom, String prenom, String telephone, String numPermis) {
        try (Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO Client (nom, prenom, telephone, num_permis) VALUES (?, ?, ?, ?)")) {
            
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, telephone);
            stmt.setString(4, numPermis);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, 
                    "Client ajouté avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de l'ajout du client: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    private static boolean updateClient(int id, String nom, String prenom, String telephone, String numPermis) {
        try (Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE Client SET nom = ?, prenom = ?, telephone = ?, num_permis = ? WHERE id = ?")) {
            
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, telephone);
            stmt.setString(4, numPermis);
            stmt.setInt(5, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, 
                    "Client modifié avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de la modification du client: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    private static boolean deleteClient(int id) {
        try (Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(
                 "DELETE FROM Client WHERE id = ?")) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, 
                    "Client supprimé avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de la suppression du client: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public static Connection connectToDatabase() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
                String url = "jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb";
                connection = DriverManager.getConnection(url);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Pilote UCanAccess non trouvé", e);
            }
        }
        return connection;
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
                displayClientsWithSearch();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Erreur lors de l'initialisation: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}