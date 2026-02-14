import javax.swing.*;
import javax.swing.table.*;
import java.util.List;
import com.healthmarketscience.jackcess.expr.ParseException;


import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.border.*;

public class LocationApp {
    // Couleurs du thème bleu marine
    private static final Color PRIMARY_COLOR = new Color(25, 55, 109);
    private static final Color SECONDARY_COLOR = new Color(45, 95, 155);
    private static final Color ACCENT_COLOR = new Color(240, 130, 50);
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color TABLE_HEADER_BG = new Color(230, 235, 240);

    // Polices
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI Semibold", Font.BOLD, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font INVOICE_FONT = new Font("Arial", Font.BOLD, 14);

    public static void displayLocations(JFrame parentFrame) {
        JFrame frame = new JFrame("Gestion des Locations • AyaCar");
        frame.setSize(1000, 650);
        frame.setLocationRelativeTo(parentFrame);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        // ==================== HEADER ====================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JLabel titleLabel = new JLabel("GESTION DES LOCATIONS");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        JButton retourBtn = createStyledButton("Retour", TEXT_COLOR, new Color(245, 245, 245));
        retourBtn.addActionListener(e -> {
            frame.dispose();
            parentFrame.setVisible(true);
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(retourBtn, BorderLayout.EAST);

        // ==================== BARRE DE RECHERCHE ====================
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setFont(LABEL_FONT);
        
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setFont(LABEL_FONT);
        
        JComboBox<String> searchCriteria = new JComboBox<>(new String[]{"Tous", "ID", "Véhicule", "Client", "Date Début", "Date Fin"});
        searchCriteria.setFont(LABEL_FONT);
        searchCriteria.setPreferredSize(new Dimension(120, 30));
        
        JButton searchBtn = createStyledButton("Rechercher", Color.BLACK, SECONDARY_COLOR);
        searchBtn.setPreferredSize(new Dimension(100, 30));
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchCriteria);
        searchPanel.add(searchBtn);

        // ==================== TABLEAU ====================
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Véhicule", "Client", "Date Début", "Date Fin", "Montant", "Admin ID"}, 0) {
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
        table.setSelectionForeground(TEXT_COLOR);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(210, 210, 210)),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));

        loadLocationsFromDatabase(model);

        // TableRowSorter pour la recherche
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        // Action du bouton de recherche
        searchBtn.addActionListener(e -> performSearch(searchField.getText(), searchCriteria.getSelectedIndex(), sorter));
        
        // Action lors de la saisie pour recherche dynamique
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch(searchField.getText(), searchCriteria.getSelectedIndex(), sorter);
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // ==================== BOUTONS ====================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton printBtn = createStyledButton("Imprimer Facture", TEXT_COLOR, ACCENT_COLOR);
        printBtn.addActionListener(e -> printInvoice(table, model));
        
        JButton editBtn = createStyledButton("Modifier", TEXT_COLOR, SECONDARY_COLOR);
        editBtn.addActionListener(e -> modifyLocation(table, model));
        
        JButton deleteBtn = createStyledButton("Supprimer", TEXT_COLOR, new Color(255, 100, 100));
        deleteBtn.addActionListener(e -> deleteLocation(table, model));
        
        JButton addBtn = createStyledButton("Ajouter", TEXT_COLOR, new Color(100, 180, 100));
        addBtn.addActionListener(e -> addNewLocation(frame, model));

        // Bouton pour réinitialiser la recherche
        JButton resetBtn = createStyledButton("Réinitialiser", TEXT_COLOR, new Color(190, 190, 190));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            searchCriteria.setSelectedIndex(0);
            performSearch("", 0, sorter);
        });
        
        for (JButton btn : new JButton[]{printBtn, editBtn, deleteBtn, addBtn, resetBtn}) {
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 20, 5, 20)
            ));
        }
        
        buttonPanel.add(printBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(resetBtn);

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

    // Méthode pour effectuer la recherche
    private static void performSearch(String searchText, int criteriaIndex, TableRowSorter<DefaultTableModel> sorter) {
        if (searchText.trim().isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        
        RowFilter<DefaultTableModel, Object> filter;
        
        if (criteriaIndex == 0) { // "Tous"
            List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
            for (int i = 0; i < sorter.getModel().getColumnCount(); i++) {
                filters.add(RowFilter.regexFilter("(?i)" + searchText, i));
            }
            filter = RowFilter.orFilter(filters);
        } else {
            filter = RowFilter.regexFilter("(?i)" + searchText, criteriaIndex - 1);
        }
        
        sorter.setRowFilter(filter);
    }

    private static void addNewLocation(JFrame parentFrame, DefaultTableModel model) {
        JDialog dialog = new JDialog(parentFrame, "Ajouter une nouvelle location", true);
        dialog.setSize(500, 550); // Augmenté la taille pour le nouveau champ
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        // Panel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Champs du formulaire
        JLabel vehicleLabel = new JLabel("Véhicule:");
        vehicleLabel.setFont(LABEL_FONT);
        JComboBox<String> vehicleCombo = new JComboBox<>();
        loadVehiclesIntoCombo(vehicleCombo);

        JLabel clientLabel = new JLabel("Client:");
        clientLabel.setFont(LABEL_FONT);
        JComboBox<String> clientCombo = new JComboBox<>();
        loadClientsIntoCombo(clientCombo);

        JLabel adminLabel = new JLabel("ID Administrateur:");
        adminLabel.setFont(LABEL_FONT);
        JTextField adminField = new JTextField();

        JLabel startDateLabel = new JLabel("Date début (jj/mm/aaaa):");
        startDateLabel.setFont(LABEL_FONT);
        JTextField startDateField = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        JLabel endDateLabel = new JLabel("Date fin (jj/mm/aaaa):");
        endDateLabel.setFont(LABEL_FONT);
        JTextField endDateField = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        JLabel amountLabel = new JLabel("Montant (€):");
        amountLabel.setFont(LABEL_FONT);
        JTextField amountField = new JTextField();

        // Ajout des composants au panel
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(vehicleLabel, gbc);
        gbc.gridy++;
        mainPanel.add(clientLabel, gbc);
        gbc.gridy++;
        mainPanel.add(adminLabel, gbc);
        gbc.gridy++;
        mainPanel.add(startDateLabel, gbc);
        gbc.gridy++;
        mainPanel.add(endDateLabel, gbc);
        gbc.gridy++;
        mainPanel.add(amountLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        mainPanel.add(vehicleCombo, gbc);
        gbc.gridy++;
        mainPanel.add(clientCombo, gbc);
        gbc.gridy++;
        mainPanel.add(adminField, gbc);
        gbc.gridy++;
        mainPanel.add(startDateField, gbc);
        gbc.gridy++;
        mainPanel.add(endDateField, gbc);
        gbc.gridy++;
        mainPanel.add(amountField, gbc);

        // Validation des dates
        JLabel validationLabel = new JLabel();
        validationLabel.setForeground(Color.RED);
        validationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        mainPanel.add(validationLabel, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton saveBtn = createStyledButton("Enregistrer", Color.BLACK, new Color(70, 160, 70));
        saveBtn.addActionListener(e -> {
            try {
                // Validation des champs
                if (vehicleCombo.getSelectedItem() == null || clientCombo.getSelectedItem() == null || 
                    adminField.getText().isEmpty() || startDateField.getText().isEmpty() || 
                    endDateField.getText().isEmpty() || amountField.getText().isEmpty()) {
                    validationLabel.setText("Tous les champs sont obligatoires");
                    return;
                }

                // Vérification des dates
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date startDate = sdf.parse(startDateField.getText());
                Date endDate = sdf.parse(endDateField.getText());
                
                if (startDate.after(endDate)) {
                    validationLabel.setText("La date de fin doit être après la date de début");
                    return;
                }

                // Récupération des IDs
                String vehicleInfo = (String) vehicleCombo.getSelectedItem();
                int vehicleId = Integer.parseInt(vehicleInfo.split(" - ")[0]);
                
                String clientInfo = (String) clientCombo.getSelectedItem();
                int clientId = Integer.parseInt(clientInfo.split(" - ")[0]);
                
                int adminId = Integer.parseInt(adminField.getText());
                double amount = Double.parseDouble(amountField.getText());

                // Insertion dans la base de données
                if (insertLocationIntoDB(vehicleId, clientId, adminId, startDate, endDate, amount)) {
                    loadLocationsFromDatabase(model); // Rafraîchir le tableau
                    dialog.dispose();
                    showInfoDialog(parentFrame, "Location ajoutée avec succès");
                }
            } catch (NumberFormatException ex) {
                validationLabel.setText("ID Admin ou Montant invalide");
            } catch (java.text.ParseException ex) {
                validationLabel.setText("Format de date invalide (jj/mm/aaaa requis)");
            } catch (Exception ex) {
                showErrorDialog(dialog, "Erreur: " + ex.getMessage());
            }
        });

        JButton cancelBtn = createStyledButton("Annuler", TEXT_COLOR, new Color(240, 240, 240));
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        // Assemblage
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static void loadVehiclesIntoCombo(JComboBox<String> combo) {
        String query = "SELECT id_Vehicule, marque, modele FROM Vehicule WHERE disponible = true";
        try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            combo.removeAllItems();
            while (rs.next()) {
                combo.addItem(rs.getInt("id_Vehicule") + " - " + 
                             rs.getString("marque") + " " + rs.getString("modele"));
            }
        } catch (SQLException e) {
            showErrorDialog(null, "Erreur lors du chargement des véhicules: " + e.getMessage());
        }
    }

    private static void loadClientsIntoCombo(JComboBox<String> combo) {
        String query = "SELECT id, nom, prenom FROM Client ORDER BY nom, prenom";
        try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            combo.removeAllItems();
            while (rs.next()) {
                combo.addItem(rs.getInt("id") + " - " + 
                             rs.getString("nom") + " " + rs.getString("prenom"));
            }
        } catch (SQLException e) {
            showErrorDialog(null, "Erreur lors du chargement des clients: " + e.getMessage());
        }
    }

    private static boolean insertLocationIntoDB(int vehicleId, int clientId, int adminId, Date startDate, Date endDate, double amount) {
        String query = "INSERT INTO Location (id_Vehicule, id, id_Users, date_debut, date_fin, prix_total) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, vehicleId);
            stmt.setInt(2, clientId);
            stmt.setInt(3, adminId);
            stmt.setDate(4, new java.sql.Date(startDate.getTime()));
            stmt.setDate(5, new java.sql.Date(endDate.getTime()));
            stmt.setDouble(6, amount);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Mettre à jour la disponibilité du véhicule
                String updateQuery = "UPDATE Vehicule SET disponible = false WHERE id_Vehicule = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, vehicleId);
                    updateStmt.executeUpdate();
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            showErrorDialog(null, "Erreur lors de l'ajout: " + e.getMessage());
            return false;
        }
    }

    private static void loadLocationsFromDatabase(DefaultTableModel model) {
        String query = "SELECT L.id_Location, V.marque, C.nom, C.prenom, L.date_debut, L.date_fin, L.prix_total, L.id_Users " +
                      "FROM Location L " +
                      "JOIN Vehicule V ON L.id_Vehicule = V.id_Vehicule " +
                      "JOIN Client C ON L.id = C.id";
        
        try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            model.setRowCount(0);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_Location"),
                    rs.getString("marque"),
                    rs.getString("nom") + " " + rs.getString("prenom"),
                    rs.getDate("date_debut"),
                    rs.getDate("date_fin"),
                    String.format("%.2f €", rs.getDouble("prix_total")),
                    rs.getInt("id_Users")
                });
            }
        } catch (SQLException e) {
            showErrorDialog(null, "Erreur lors du chargement des locations: " + e.getMessage());
        }
    }

    private static void printInvoice(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            try {
                // Conversion de l'indice de ligne du modèle de vue vers le modèle de données
                int modelRow = table.convertRowIndexToModel(selectedRow);
                
                // Création d'un JPanel personnalisé pour la facture
                JPanel invoicePanel = new JPanel(new BorderLayout());
                invoicePanel.setBackground(Color.WHITE);
                invoicePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                // En-tête de la facture
                JPanel headerPanel = new JPanel(new BorderLayout());
                headerPanel.setBackground(Color.WHITE);
                headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
                
                JLabel titleLabel = new JLabel("FACTURE AyaCar");
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                titleLabel.setForeground(PRIMARY_COLOR);
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                
                JLabel subTitleLabel = new JLabel("Location de véhicule");
                subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                subTitleLabel.setForeground(SECONDARY_COLOR);
                subTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                
                JPanel titlePanel = new JPanel();
                titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
                titlePanel.setBackground(Color.WHITE);
                titlePanel.add(titleLabel);
                titlePanel.add(subTitleLabel);
                
                headerPanel.add(titlePanel, BorderLayout.CENTER);
                
                // Logo
                JLabel logoLabel = new JLabel("AYACAR", SwingConstants.RIGHT);
                logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                logoLabel.setForeground(ACCENT_COLOR);
                headerPanel.add(logoLabel, BorderLayout.EAST);
                
                invoicePanel.add(headerPanel, BorderLayout.NORTH);
                
                // Informations de la facture
                JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 10));
                infoPanel.setBackground(Color.WHITE);
                infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                
                // Informations client
                JPanel clientPanel = createInfoPanel("CLIENT", model.getValueAt(modelRow, 2).toString());
                
                // Informations location
                String locationInfo = String.format(
                    "<html><b>ID Location:</b> %s<br><b>Véhicule:</b> %s<br><b>Dates:</b> %s au %s</html>",
                    model.getValueAt(modelRow, 0),
                    model.getValueAt(modelRow, 1),
                    model.getValueAt(modelRow, 3),
                    model.getValueAt(modelRow, 4)
                );
                JPanel locationPanel = createInfoPanel("LOCATION", locationInfo);
                
                // Informations facture
                String invoiceInfo = String.format(
                    "<html><b>Date facture:</b> %s<br><b>Admin ID:</b> %s</html>",
                    new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
                    model.getValueAt(modelRow, 6)
                );
                JPanel invoiceInfoPanel = createInfoPanel("FACTURE", invoiceInfo);
                
                // Montant
                JPanel amountPanel = new JPanel(new BorderLayout());
                amountPanel.setBackground(new Color(245, 245, 245));
                amountPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                
                JLabel amountTitle = new JLabel("MONTANT TOTAL");
                amountTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
                amountTitle.setForeground(PRIMARY_COLOR);
                
                JLabel amountValue = new JLabel(model.getValueAt(modelRow, 5).toString());
                amountValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
                amountValue.setForeground(ACCENT_COLOR);
                amountValue.setHorizontalAlignment(SwingConstants.RIGHT);
                
                amountPanel.add(amountTitle, BorderLayout.WEST);
                amountPanel.add(amountValue, BorderLayout.EAST);
                
                infoPanel.add(clientPanel);
                infoPanel.add(locationPanel);
                infoPanel.add(invoiceInfoPanel);
                infoPanel.add(amountPanel);
                
                invoicePanel.add(infoPanel, BorderLayout.CENTER);
                
                // Pied de page
                JPanel footerPanel = new JPanel();
                footerPanel.setBackground(new Color(245, 245, 245));
                footerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(15, 0, 0, 0)
                ));
                
                JLabel footerLabel = new JLabel("<html><center>Merci pour votre confiance !<br>AyaCar - Location de véhicules haut de gamme</center></html>");
                footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                footerLabel.setForeground(new Color(120, 120, 120));
                footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
                
                footerPanel.add(footerLabel);
                invoicePanel.add(footerPanel, BorderLayout.SOUTH);
                
                // Aperçu avant impression
                JDialog previewDialog = new JDialog();
                previewDialog.setTitle("Aperçu de la facture");
                previewDialog.setSize(600, 700);
                previewDialog.setLocationRelativeTo(null);
                previewDialog.setModal(true);
                
                JButton printBtn = new JButton("Imprimer");
                printBtn.setFont(BUTTON_FONT);
                printBtn.setBackground(PRIMARY_COLOR);
                printBtn.setForeground(Color.BLUE);
                printBtn.addActionListener(e -> {
                    printComponent(invoicePanel);
                    previewDialog.dispose();
                });
                
                JButton closeBtn = new JButton("Fermer");
                closeBtn.setFont(BUTTON_FONT);
                closeBtn.setBackground(SECONDARY_COLOR);
                closeBtn.setForeground(Color.BLUE);
                closeBtn.addActionListener(e -> previewDialog.dispose());
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                buttonPanel.setBackground(Color.WHITE);
                buttonPanel.add(printBtn);
                buttonPanel.add(closeBtn);
                
                JScrollPane scrollPane = new JScrollPane(invoicePanel);
                scrollPane.setBorder(null);
                
                previewDialog.add(scrollPane, BorderLayout.CENTER);
                previewDialog.add(buttonPanel, BorderLayout.SOUTH);
                previewDialog.setVisible(true);
                
            } catch (Exception ex) {
                showErrorDialog(null, "Erreur lors de la génération de la facture: " + ex.getMessage());
            }
        } else {
            showErrorDialog(null, "Veuillez sélectionner une location");
        }
    }

    private static JPanel createInfoPanel(String title, String content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(SECONDARY_COLOR);
        
        JLabel contentLabel = new JLabel(content);
        contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentLabel, BorderLayout.CENTER);
        
        return panel;
    }

    private static void printComponent(Component component) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setJobName("Facture AyaCar");
        
        // Configurer le format de page avec des marges
        PageFormat pageFormat = printerJob.defaultPage();
        pageFormat.setOrientation(PageFormat.PORTRAIT);
        
        // Définir le Printable
        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) 
                    throws PrinterException {
                if (pageIndex > 0) {
                    return Printable.NO_SUCH_PAGE;
                }
                
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                
                // Calculer le ratio de mise à l'échelle
                double scaleX = pageFormat.getImageableWidth() / component.getWidth();
                double scaleY = pageFormat.getImageableHeight() / component.getHeight();
                double scale = Math.min(scaleX, scaleY);
                
                // Appliquer la mise à l'échelle si nécessaire
                if (scale < 1.0) {
                    g2d.scale(scale, scale);
                }
                
                // Imprimer le composant
                component.printAll(g2d);
                
                return Printable.PAGE_EXISTS;
            }
        });
        
        // Afficher la boîte de dialogue d'impression
        if (printerJob.printDialog()) {
            try {
                printerJob.print();
            } catch (PrinterException ex) {
                showErrorDialog(null, "Erreur lors de l'impression: " + ex.getMessage());
            }
        }
    }
    private static void modifyLocation(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorDialog(null, "Veuillez sélectionner une location");
            return;
        }
        
        // Conversion de l'indice de ligne du modèle de vue vers le modèle de données
        int modelRow = table.convertRowIndexToModel(selectedRow);
        
        // Récupérer l'ID de la location sélectionnée
        int locationId = (int) model.getValueAt(modelRow, 0);
        
        // Créer la boîte de dialogue de modification
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(table), "Modifier la location", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(table);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Panel principal avec GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Champs du formulaire
        JLabel vehicleLabel = new JLabel("Véhicule:");
        vehicleLabel.setFont(LABEL_FONT);
        JComboBox<String> vehicleCombo = new JComboBox<>();
        loadVehiclesIntoCombo(vehicleCombo);
        
        JLabel clientLabel = new JLabel("Client:");
        clientLabel.setFont(LABEL_FONT);
        JComboBox<String> clientCombo = new JComboBox<>();
        loadClientsIntoCombo(clientCombo);
        
        JLabel adminLabel = new JLabel("ID Administrateur:");
        adminLabel.setFont(LABEL_FONT);
        JTextField adminField = new JTextField();
        
        JLabel startDateLabel = new JLabel("Date début (jj/mm/aaaa):");
        startDateLabel.setFont(LABEL_FONT);
        JTextField startDateField = new JTextField();
        
        JLabel endDateLabel = new JLabel("Date fin (jj/mm/aaaa):");
        endDateLabel.setFont(LABEL_FONT);
        JTextField endDateField = new JTextField();
        
        JLabel amountLabel = new JLabel("Montant (€):");
        amountLabel.setFont(LABEL_FONT);
        JTextField amountField = new JTextField();
        
        // Récupérer les données actuelles de la location
        try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb")) {
            String query = "SELECT L.*, V.marque, V.modele, C.nom, C.prenom " +
                          "FROM Location L " +
                          "JOIN Vehicule V ON L.id_Vehicule = V.id_Vehicule " +
                          "JOIN Client C ON L.id = C.id " +
                          "WHERE L.id_Location = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, locationId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Définir les valeurs actuelles
                        int vehicleId = rs.getInt("id_Vehicule");
                        String vehicleInfo = vehicleId + " - " + rs.getString("marque") + " " + rs.getString("modele");
                        setSelectedItemByPrefix(vehicleCombo, String.valueOf(vehicleId));
                        
                        int clientId = rs.getInt("id");
                        String clientInfo = clientId + " - " + rs.getString("nom") + " " + rs.getString("prenom");
                        setSelectedItemByPrefix(clientCombo, String.valueOf(clientId));
                        
                        adminField.setText(String.valueOf(rs.getInt("id_Users")));
                        
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date startDate = rs.getDate("date_debut");
                        startDateField.setText(sdf.format(startDate));
                        
                        Date endDate = rs.getDate("date_fin");
                        endDateField.setText(sdf.format(endDate));
                        
                        double amount = rs.getDouble("prix_total");
                        amountField.setText(String.format("%.2f", amount));
                    }
                }
            }
        } catch (SQLException e) {
            showErrorDialog(dialog, "Erreur lors du chargement des données: " + e.getMessage());
        }
        
        // Ajout des composants au panel
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(vehicleLabel, gbc);
        gbc.gridy++;
        mainPanel.add(clientLabel, gbc);
        gbc.gridy++;
        mainPanel.add(adminLabel, gbc);
        gbc.gridy++;
        mainPanel.add(startDateLabel, gbc);
        gbc.gridy++;
        mainPanel.add(endDateLabel, gbc);
        gbc.gridy++;
        mainPanel.add(amountLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        mainPanel.add(vehicleCombo, gbc);
        gbc.gridy++;
        mainPanel.add(clientCombo, gbc);
        gbc.gridy++;
        mainPanel.add(adminField, gbc);
        gbc.gridy++;
        mainPanel.add(startDateField, gbc);
        gbc.gridy++;
        mainPanel.add(endDateField, gbc);
        gbc.gridy++;
        mainPanel.add(amountField, gbc);
        
        // Label pour les messages de validation
        JLabel validationLabel = new JLabel();
        validationLabel.setForeground(Color.RED);
        validationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        mainPanel.add(validationLabel, gbc);
        
        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton saveBtn = createStyledButton("Enregistrer", Color.BLACK, new Color(70, 160, 70));
        saveBtn.addActionListener(e -> {
            try {
                // Validation des champs
                if (vehicleCombo.getSelectedItem() == null || clientCombo.getSelectedItem() == null || 
                    adminField.getText().isEmpty() || startDateField.getText().isEmpty() || 
                    endDateField.getText().isEmpty() || amountField.getText().isEmpty()) {
                    validationLabel.setText("Tous les champs sont obligatoires");
                    return;
                }
                
                // Vérification des dates
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date startDate = sdf.parse(startDateField.getText());
                Date endDate = sdf.parse(endDateField.getText());
                
                if (startDate.after(endDate)) {
                    validationLabel.setText("La date de fin doit être après la date de début");
                    return;
                }
                
                // Récupération des IDs
                String vehicleInfo = (String) vehicleCombo.getSelectedItem();
                int vehicleId = Integer.parseInt(vehicleInfo.split(" - ")[0]);
                
                String clientInfo = (String) clientCombo.getSelectedItem();
                int clientId = Integer.parseInt(clientInfo.split(" - ")[0]);
                
                int adminId = Integer.parseInt(adminField.getText());
                double amount = Double.parseDouble(amountField.getText().replace(",", "."));
                
                // Mise à jour dans la base de données
                if (updateLocationInDB(locationId, vehicleId, clientId, adminId, startDate, endDate, amount)) {
                    loadLocationsFromDatabase(model); // Rafraîchir le tableau
                    dialog.dispose();
                    showInfoDialog((JFrame) SwingUtilities.getWindowAncestor(table), "Location modifiée avec succès");
                }
            } catch (NumberFormatException ex) {
                validationLabel.setText("ID Admin ou Montant invalide");
            } catch (java.text.ParseException ex) {
                validationLabel.setText("Format de date invalide (jj/mm/aaaa requis)");
            } catch (Exception ex) {
                showErrorDialog(dialog, "Erreur: " + ex.getMessage());
            }
        });
        
        JButton cancelBtn = createStyledButton("Annuler", TEXT_COLOR, new Color(240, 240, 240));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        // Assemblage
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Méthode pour mettre à jour la location dans la base de données
    private static boolean updateLocationInDB(int locationId, int vehicleId, int clientId, int adminId, 
                                          Date startDate, Date endDate, double amount) {
        // D'abord, récupérer l'ancien véhicule pour mettre à jour sa disponibilité
        int oldVehicleId = -1;
        try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb")) {
            String queryGetOld = "SELECT id_Vehicule FROM Location WHERE id_Location = ?";
            try (PreparedStatement stmt = conn.prepareStatement(queryGetOld)) {
                stmt.setInt(1, locationId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        oldVehicleId = rs.getInt("id_Vehicule");
                    }
                }
            }
            
            // Mise à jour de la location
            String queryUpdate = "UPDATE Location SET id_Vehicule = ?, id = ?, id_Users = ?, " +
                               "date_debut = ?, date_fin = ?, prix_total = ? WHERE id_Location = ?";
            try (PreparedStatement stmt = conn.prepareStatement(queryUpdate)) {
                stmt.setInt(1, vehicleId);
                stmt.setInt(2, clientId);
                stmt.setInt(3, adminId);
                stmt.setDate(4, new java.sql.Date(startDate.getTime()));
                stmt.setDate(5, new java.sql.Date(endDate.getTime()));
                stmt.setDouble(6, amount);
                stmt.setInt(7, locationId);
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Si le véhicule a changé, mettre à jour les disponibilités
                    if (oldVehicleId != vehicleId && oldVehicleId != -1) {
                        // Rendre l'ancien véhicule disponible
                        String updateOldQuery = "UPDATE Vehicule SET disponible = true WHERE id_Vehicule = ?";
                        try (PreparedStatement updateOldStmt = conn.prepareStatement(updateOldQuery)) {
                            updateOldStmt.setInt(1, oldVehicleId);
                            updateOldStmt.executeUpdate();
                        }
                        
                        // Rendre le nouveau véhicule indisponible
                        String updateNewQuery = "UPDATE Vehicule SET disponible = false WHERE id_Vehicule = ?";
                        try (PreparedStatement updateNewStmt = conn.prepareStatement(updateNewQuery)) {
                            updateNewStmt.setInt(1, vehicleId);
                            updateNewStmt.executeUpdate();
                        }
                    }
                    return true;
                }
                return false;
            }
        } catch (SQLException e) {
            showErrorDialog(null, "Erreur lors de la mise à jour: " + e.getMessage());
            return false;
        }
    }

    // Méthode utilitaire pour sélectionner un élément dans un combo box par son préfixe
    private static void setSelectedItemByPrefix(JComboBox<String> comboBox, String prefix) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            String item = comboBox.getItemAt(i);
            if (item.startsWith(prefix + " ")) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private static void deleteLocation(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            // Conversion de l'indice de ligne du modèle de vue vers le modèle de données
            int modelRow = table.convertRowIndexToModel(selectedRow);
            
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Êtes-vous sûr de vouloir supprimer cette location?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                int idLocation = (int) model.getValueAt(modelRow, 0);
                if (deleteLocationFromDB(idLocation)) {
                    ((DefaultTableModel) table.getModel()).removeRow(modelRow);
                    showInfoDialog(null, "Location supprimée avec succès");
                }
            }
        } else {
            showErrorDialog(null, "Veuillez sélectionner une location");
        }
    }

    private static boolean deleteLocationFromDB(int idLocation) {
        String query = "DELETE FROM Location WHERE id_Location = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idLocation);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            showErrorDialog(null, "Erreur lors de la suppression: " + e.getMessage());
            return false;
        }
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

    private static void showInfoDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, 
            "<html><div style='width:200px;'>" + message + "</div></html>",
            "Information", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, 
            "<html><div style='width:200px;color:red;'>" + message + "</div></html>",
            "Erreur", 
            JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                JFrame dummyFrame = new JFrame();
                dummyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                displayLocations(dummyFrame);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}