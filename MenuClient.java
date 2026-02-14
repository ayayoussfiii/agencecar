import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MenuClient extends JFrame {

    // Palette de couleurs
    private static final Color PRIMARY_COLOR = new Color(25, 55, 109);
    private static final Color SECONDARY_COLOR = new Color(45, 95, 155);
    private static final Color ACCENT_COLOR = new Color(240, 130, 50);
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 245);
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color TEXT_LIGHT = new Color(250, 250, 250);
    
    // Polices
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    
    private JPanel contentPanel;
    private int clientId;
    private Connection connection;

    public MenuClient(int clientId, Connection connection) {
        this.clientId = clientId;
        this.connection = connection;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("AYACAR - Espace Client");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BACKGROUND_COLOR);
        setContentPane(mainPanel);
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createSideMenuPanel(), BorderLayout.WEST);
        
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.add(createWelcomePanel(), BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(createStatusBarPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(getWidth(), 70));
        
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        logoPanel.setBackground(PRIMARY_COLOR);
        logoPanel.setBorder(new EmptyBorder(0, 15, 0, 0));
        
        JLabel logoLabel = new JLabel("AYACAR CLIENT");
        logoLabel.setFont(TITLE_FONT);
        logoLabel.setForeground(TEXT_LIGHT);
        logoPanel.add(logoLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(PRIMARY_COLOR);
        rightPanel.setBorder(new EmptyBorder(15, 0, 15, 20));
        
        JLabel dateLabel = new JLabel(new SimpleDateFormat("dd MMMM yyyy").format(new java.util.Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(new Color(200, 200, 200));
        
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 25));
        separator.setForeground(new Color(100, 140, 180));
        
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        userInfo.setBackground(PRIMARY_COLOR);
        
        // Récupérer le nom du client depuis la base
        String clientName = getClientName();
        
        JPanel userIconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(ACCENT_COLOR);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.setColor(TEXT_LIGHT);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String text = clientName.substring(0, 1).toUpperCase();
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.drawString(text, (getWidth() - textWidth)/2, (getHeight() + textHeight/2)/2 - 1);
                g2d.dispose();
            }
        };
        userIconPanel.setPreferredSize(new Dimension(28, 28));
        
        JLabel userLabel = new JLabel(clientName);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(TEXT_LIGHT);
        
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setBackground(new Color(200, 70, 70));
        logoutButton.setBorder(new EmptyBorder(6, 12, 6, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            dispose();
        });
        
        userInfo.add(userIconPanel);
        userInfo.add(userLabel);
        
        rightPanel.add(dateLabel);
        rightPanel.add(separator);
        rightPanel.add(userInfo);
        rightPanel.add(logoutButton);
        
        header.add(logoPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }

    private String getClientName() {
        String query = "SELECT nom, prenom FROM Client WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("prenom") + " " + rs.getString("nom");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur d'accès aux données client: " + e.getMessage(),
                "Erreur BD", JOptionPane.ERROR_MESSAGE);
        }
        return "Client";
    }

    private JPanel createSideMenuPanel() {
        JPanel sideMenu = new JPanel();
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));
        sideMenu.setBackground(BACKGROUND_COLOR);
        sideMenu.setPreferredSize(new Dimension(220, getHeight()));
        sideMenu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("MENU CLIENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sideMenu.add(titleLabel);
        sideMenu.add(Box.createVerticalStrut(15));

        String[] menuOptions = {
        	    "1 - Véhicules disponibles",
        	    "2 - Mes locations", 
        	    "3 - Liste des clients",
        	    "4 - Mes réclamations"  // Ajouter l'espace et utiliser le même texte
        	};

        for (String option : menuOptions) {
            JPanel menuButton = createMenuButton(option);
            menuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            sideMenu.add(menuButton);
            sideMenu.add(Box.createVerticalStrut(8));
        }

        sideMenu.add(Box.createVerticalStrut(15));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(190, 1));
        separator.setForeground(new Color(200, 200, 200));
        sideMenu.add(separator);
        sideMenu.add(Box.createVerticalStrut(15));

        JLabel helpLabel = new JLabel("Besoin d'aide ?");
        helpLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        helpLabel.setForeground(PRIMARY_COLOR);
        helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sideMenu.add(helpLabel);
        
        return sideMenu;
    }

    private JPanel createMenuButton(String text) {
        JPanel button = new JPanel(new BorderLayout());
        button.setBackground(BACKGROUND_COLOR);
        button.setBorder(new EmptyBorder(5, 0, 5, 0));
        button.setMaximumSize(new Dimension(190, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLabel.setForeground(TEXT_DARK);
        
        button.add(textLabel, BorderLayout.WEST);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMenuAction(text);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                textLabel.setForeground(ACCENT_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                textLabel.setForeground(TEXT_DARK);
            }
        });
        
        return button;
    }

    private void handleMenuAction(String action) {
        if (action.startsWith("1 - Véhicules disponibles")) {
            showVehiclesPanel();
        } 
        else if (action.startsWith("2 - Mes locations")) {
            showClientRentalsPanel();
        }
        else if (action.startsWith("3 - Liste des clients")) {
            showClientsListPanel();
        }
        else if (action.startsWith("4 - Mes réclamations")) {
            showReclamationsPanel();
        }
    }
    private void showReclamationsPanel() {
        JPanel reclamationPanel = new JPanel(new BorderLayout(0, 15));
        reclamationPanel.setBackground(BACKGROUND_COLOR);
        
        // En-tête
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Mes Réclamations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JButton newReclamationBtn = new JButton("Nouvelle réclamation");
        newReclamationBtn.setFont(BUTTON_FONT);
        newReclamationBtn.setBackground(ACCENT_COLOR);
        newReclamationBtn.setForeground(Color.BLACK);
        newReclamationBtn.setBorder(new EmptyBorder(8, 15, 8, 15));
        newReclamationBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newReclamationBtn.setFocusPainted(false);
        newReclamationBtn.addActionListener(e -> showNewReclamationForm());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(newReclamationBtn, BorderLayout.EAST);
        
        // Table de réclamations
        try {
            String query = "SELECT id_Reclamation, date_Reclamation, Sujet, Message, Etat FROM Reclamation WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, String.valueOf(clientId));
            ResultSet rs = stmt.executeQuery();
            
            List<Object[]> data = new ArrayList<>();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_Reclamation"),
                    new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("date_Reclamation")),
                    rs.getString("Sujet"),
                    rs.getString("Message"),
                    rs.getString("Etat")
                };
                data.add(row);
            }
            
            String[] columns = {"ID", "Date", "Sujet", "Message", "État"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (Object[] row : data) {
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            table.setFont(TABLE_FONT);
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            // Définir la largeur des colonnes
            table.getColumnModel().getColumn(0).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setPreferredWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(300);
            table.getColumnModel().getColumn(4).setPreferredWidth(100);
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            
            // Info panel
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.setBackground(new Color(245, 250, 255));
            infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240)),
                new EmptyBorder(10, 15, 10, 15)
            ));
            
            JLabel infoLabel = new JLabel("<html>Vous pouvez soumettre une réclamation concernant nos services. "
                    + "Notre équipe vous répondra dans les plus brefs délais.</html>");
            infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            infoLabel.setForeground(new Color(50, 100, 150));
            infoPanel.add(infoLabel);
            
            reclamationPanel.add(headerPanel, BorderLayout.NORTH);
            reclamationPanel.add(scrollPane, BorderLayout.CENTER);
            reclamationPanel.add(infoPanel, BorderLayout.SOUTH);
            
            contentPanel.removeAll();
            contentPanel.add(reclamationPanel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des réclamations: " + e.getMessage(),
                "Erreur BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showNewReclamationForm() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Nouvelle Réclamation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.black );
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        
        // Sujet
        JLabel sujetLabel = new JLabel("Sujet de la réclamation:");
        sujetLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sujetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField sujetField = new JTextField();
        sujetField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sujetField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        sujetField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Message
        JLabel messageLabel = new JLabel("Détails de la réclamation:");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea messageArea = new JTextArea();
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        messageScrollPane.setPreferredSize(new Dimension(580, 150));
        messageScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        // Boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(new Color(240, 240, 240));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> showReclamationsPanel());
        
        JButton submitButton = new JButton("Soumettre");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setBackground(ACCENT_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> {
            submitReclamation(sujetField.getText(), messageArea.getText());
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(separator);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(sujetLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(sujetField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(messageLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(messageScrollPane);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(buttonPanel);
        
        contentPanel.removeAll();
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void submitReclamation(String sujet, String message) {
        if (sujet.trim().isEmpty() || message.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez remplir tous les champs obligatoires.",
                "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String query = "INSERT INTO Reclamation (id, date_Reclamation, Sujet, Message, Etat) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            
            stmt.setString(1, String.valueOf(clientId));
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, sujet);
            stmt.setString(4, message);
            stmt.setString(5, "en cours");
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Votre réclamation a été soumise avec succès.\nNous vous répondrons dans les plus brefs délais.",
                    "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                showReclamationsPanel();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la soumission de votre réclamation.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la soumission de la réclamation: " + e.getMessage(),
                "Erreur BD", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showClientsListPanel() {
        try {
            String query = "SELECT id, nom, prenom, telephone, num_permis FROM Client";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            List<Object[]> data = new ArrayList<>();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("telephone"),
                    rs.getString("num_permis")
                };
                data.add(row);
            }
            
            String[] columns = {"ID", "Nom", "Prénom", "Num_Permis", "Téléphone"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (Object[] row : data) {
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            table.setFont(TABLE_FONT);
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            contentPanel.removeAll();
            contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des clients: " + e.getMessage(),
                "Erreur BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showVehiclesPanel() {
        try {
            String query = "SELECT marque, modele, immatriculation, prix_journalier FROM Vehicule WHERE disponible = TRUE";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            List<Object[]> data = new ArrayList<>();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("marque"),
                    rs.getString("modele"),
                    rs.getString("immatriculation"),
                    String.format("%.2f €", rs.getDouble("prix_journalier"))
                };
                data.add(row);
            }
            
            String[] columns = {"Marque", "Modèle", "Immatriculation", "Prix/jour"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (Object[] row : data) {
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            table.setFont(TABLE_FONT);
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            // Création d'un label informatif à la place du bouton de réservation
            JLabel infoLabel = new JLabel("Consultation uniquement - Contactez notre service client pour réserver");
            infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            infoLabel.setForeground(PRIMARY_COLOR);
            
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            infoPanel.setBackground(BACKGROUND_COLOR);
            infoPanel.add(infoLabel);
            
            contentPanel.removeAll();
            contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
            contentPanel.add(infoPanel, BorderLayout.SOUTH);
            contentPanel.revalidate();
            contentPanel.repaint();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des véhicules: " + e.getMessage());
        }
    }

    private void showClientRentalsPanel() {
        // Demander à l'utilisateur de saisir son ID
        String inputValue = JOptionPane.showInputDialog(this, 
                                                       "Veuillez saisir votre ID client:", 
                                                       "Vérification d'identité", 
                                                       JOptionPane.QUESTION_MESSAGE);
        
        // Vérifier si l'utilisateur a annulé ou n'a rien saisi
        if (inputValue == null || inputValue.trim().isEmpty()) {
            return; // Sortir de la méthode si annulation ou saisie vide
        }
        
        // Essayer de convertir l'entrée en entier
        int enteredId;
        try {
            enteredId = Integer.parseInt(inputValue.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                                         "ID client invalide. Veuillez saisir un nombre entier.", 
                                         "Erreur de saisie", 
                                         JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Pas de vérification par rapport à l'ID de connexion
        // On cherche directement les locations du client avec l'ID saisi
        
        try {
            // D'abord vérifier si le client existe avec cet ID
            String checkClientQuery = "SELECT COUNT(*) FROM Client WHERE id = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkClientQuery)) {
                checkStmt.setInt(1, enteredId);
                ResultSet checkRs = checkStmt.executeQuery();
                
                if (checkRs.next() && checkRs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, 
                                                 "Aucun client trouvé avec cet ID.", 
                                                 "Client introuvable", 
                                                 JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            // Si le client existe, afficher ses locations
            String query = "SELECT v.marque, v.modele, v.immatriculation, " +
                         "l.date_debut, l.date_fin, l.id_Users " +
                         "FROM Location l " +
                         "JOIN Vehicule v ON l.id_Vehicule = v.id_Vehicule " +
                         "WHERE l.id = ?";  // Utiliser l'ID de la location qui correspond à l'ID du client

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, enteredId);
                ResultSet rs = stmt.executeQuery();

                List<Object[]> data = new ArrayList<>();
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("marque"),
                        rs.getString("modele"),
                        rs.getString("immatriculation"),
                        new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("date_debut")),
                        new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("date_fin")),
                        rs.getInt("id_Users")
                    };
                    data.add(row);
                }

                String[] columns = {"Marque", "Modèle", "Immatriculation", "Date début", "Date fin", "ID Admin"};
                DefaultTableModel model = new DefaultTableModel(columns, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                for (Object[] row : data) {
                    model.addRow(row);
                }

                JTable table = new JTable(model);
                table.setFont(TABLE_FONT);
                table.setRowHeight(25);
                table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

                contentPanel.removeAll();
                if (data.isEmpty()) {
                    JLabel message = new JLabel("<html>Aucune location trouvée pour ce client.<br>"
                            + "Ce client existe mais n'a pas de locations enregistrées.</html>",
                            SwingConstants.CENTER);
                    message.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    contentPanel.add(message, BorderLayout.CENTER);
                } else {
                    contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
                }

                contentPanel.revalidate();
                contentPanel.repaint();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            String errorMsg = "<html>Erreur technique :<br>" + e.getMessage() +
                             "<br><br>Vérifiez que :<br>" +
                             "- Les noms de tables sont corrects (Client/Location/Vehicule)<br>" +
                             "- Les colonnes existent bien dans les tables<br>" +
                             "- Les relations sont correctement définies</html>";

            JOptionPane.showMessageDialog(this,
                    errorMsg,
                    "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
        }
    }
    private JPanel createWelcomePanel() {
        // Panel principal avec ScrollPane
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Panel de contenu
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setBackground(Color.WHITE);
        
        // En-tête avec image de bannière
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        headerPanel.setMaximumSize(new Dimension(1200, 150));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Logo et titre
        JPanel logoTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoTitlePanel.setBackground(PRIMARY_COLOR);
        
        // Simuler un logo avec un panel personnalisé
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dessiner le cercle du logo
                g2d.setColor(ACCENT_COLOR);
                g2d.fillOval(0, 0, 50, 50);
                
                // Dessiner les initiales AY
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 22));
                g2d.drawString("AY", 10, 33);
                
                g2d.dispose();
            }
        };
        logoPanel.setPreferredSize(new Dimension(50, 50));
        
        JLabel titleLabel = new JLabel("AYACAR");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        
        logoTitlePanel.add(logoPanel);
        logoTitlePanel.add(Box.createHorizontalStrut(15));
        logoTitlePanel.add(titleLabel);
        
        JLabel welcomeLabel = new JLabel("Bienvenue dans votre espace client");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Location de véhicules simple et efficace");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(logoTitlePanel);
        headerPanel.add(Box.createVerticalStrut(20));
        headerPanel.add(welcomeLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);
        
        // Panneau des actions rapides
        JPanel quickActionsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        quickActionsPanel.setBackground(Color.WHITE);
        quickActionsPanel.setBorder(new EmptyBorder(30, 40, 20, 40));
        quickActionsPanel.setMaximumSize(new Dimension(1000, 120));
        quickActionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Créer les boutons d'action rapide
        JPanel viewVehiclesCard = createActionCard("Consulter les véhicules", 
                "Découvrir notre flotte de véhicules disponibles à la location", 
                e -> showVehiclesPanel());
                
        JPanel viewRentalsCard = createActionCard("Mes locations", 
                "Visualiser et gérer vos locations actuelles et passées", 
                e -> showClientRentalsPanel());
                
        JPanel contactCard = createActionCard("Besoin d'aide ?", 
                "Contacter notre service client pour toute assistance", 
                e -> JOptionPane.showMessageDialog(this, "Service client: +212 6 12 34 56 78\nEmail: support@ayacar.ma", 
                        "Contact", JOptionPane.INFORMATION_MESSAGE));
        
        quickActionsPanel.add(viewVehiclesCard);
        quickActionsPanel.add(viewRentalsCard);
        quickActionsPanel.add(contactCard);

        // Section d'information
        JPanel infoSection = new JPanel();
        infoSection.setLayout(new BoxLayout(infoSection, BoxLayout.Y_AXIS));
        infoSection.setBackground(Color.WHITE);
        infoSection.setBorder(new EmptyBorder(20, 40, 30, 40));
        infoSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Titre section info
        JLabel infoTitle = new JLabel("COMMENT ÇA MARCHE");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        infoTitle.setForeground(PRIMARY_COLOR);
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Séparateur
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(150, 2));
        separator.setForeground(ACCENT_COLOR);
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Étapes de location
        JPanel stepsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        stepsPanel.setBackground(Color.WHITE);
        stepsPanel.setMaximumSize(new Dimension(900, 120));
        stepsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel step1 = createStepCard("1", "Consultez notre flotte", 
                "Parcourez notre catalogue de véhicules et choisissez celui qui vous convient");
        JPanel step2 = createStepCard("2", "Contactez-nous", 
                "Appelez notre agence ou venez nous rencontrer pour réserver votre véhicule");
        JPanel step3 = createStepCard("3", "Récupérez votre véhicule", 
                "Présentez-vous à l'agence avec votre permis de conduire et une pièce d'identité");
        
        stepsPanel.add(step1);
        stepsPanel.add(step2);
        stepsPanel.add(step3);
        
        // Bannière promo
        JPanel promoPanel = new JPanel(new BorderLayout());
        promoPanel.setBackground(SECONDARY_COLOR);
        promoPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        promoPanel.setMaximumSize(new Dimension(900, 80));
        promoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel promoLabel = new JLabel("OFFRE SPÉCIALE: -15% sur les locations de longue durée (> 7 jours)");
        promoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        promoLabel.setForeground(Color.WHITE);
        
        JButton promoButton = new JButton("En savoir plus");
        promoButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        promoButton.setBackground(ACCENT_COLOR);
        promoButton.setForeground(Color.WHITE);
        promoButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        promoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        promoButton.setFocusPainted(false);
        
        promoPanel.add(promoLabel, BorderLayout.WEST);
        promoPanel.add(promoButton, BorderLayout.EAST);
        
        // Assemblage de la section info
        infoSection.add(infoTitle);
        infoSection.add(Box.createVerticalStrut(10));
        infoSection.add(separator);
        infoSection.add(Box.createVerticalStrut(25));
        infoSection.add(stepsPanel);
        infoSection.add(Box.createVerticalStrut(30));
        infoSection.add(promoPanel);
        
        // Footer avec coordonnées
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setBorder(new EmptyBorder(25, 40, 25, 40));
        footerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel contactTitle = new JLabel("NOUS CONTACTER");
        contactTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        contactTitle.setForeground(PRIMARY_COLOR);
        contactTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel contactInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contactInfoPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel addressLabel = new JLabel("123 Avenue Mohammed V, Casablanca");
        addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel phoneLabel = new JLabel(" | +212 6 12 34 56 78 | ");
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel emailLabel = new JLabel("contact@ayacar.ma");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        contactInfoPanel.add(addressLabel);
        contactInfoPanel.add(phoneLabel);
        contactInfoPanel.add(emailLabel);
        
        footerPanel.add(contactTitle);
        footerPanel.add(Box.createVerticalStrut(10));
        footerPanel.add(contactInfoPanel);
        
        // Assemblage du panel principal
        contentContainer.add(headerPanel);
        contentContainer.add(quickActionsPanel);
        contentContainer.add(infoSection);
        contentContainer.add(footerPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }

    // Méthode pour créer une carte d'action rapide
    private JPanel createActionCard(String title, String description, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BACKGROUND_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(20, 15, 20, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(TEXT_DARK);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(descLabel);
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "clicked"));
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(235, 235, 240));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(BACKGROUND_COLOR);
            }
        });
        
        return card;
    }

    // Méthode pour créer une carte d'étape
    private JPanel createStepCard(String number, String title, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(20, 15, 20, 15)
        ));
        
        // Numéro de l'étape dans un cercle
        JPanel numberCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillOval(0, 0, 30, 30);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                
                // Centrer le texte
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(number);
                int textHeight = fm.getHeight();
                g2d.drawString(number, (30 - textWidth) / 2, (30 - textHeight) / 2 + fm.getAscent());
                
                g2d.dispose();
            }
        };
        numberCircle.setPreferredSize(new Dimension(30, 30));
        numberCircle.setMaximumSize(new Dimension(30, 30));
        numberCircle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(TEXT_DARK);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(numberCircle);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(descLabel);
        
        return card;
    }

    private JPanel createStatusBarPanel() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(245, 246, 247));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        statusBar.setPreferredSize(new Dimension(getWidth(), 25));
        
        JLabel statusLabel = new JLabel("© 2025 AyaCar - Version Client");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(120, 120, 120));
        statusLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        JLabel timeLabel = new JLabel(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLabel.setForeground(new Color(120, 120, 120));
        timeLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);
        
        return statusBar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                String dbPath = "C:/Users/lenovo/Documents/BDProjetAya.accdb";
                String connectionString = "jdbc:ucanaccess://" + dbPath;
                Connection connection = DriverManager.getConnection(connectionString);
                
                MenuClient clientGUI = new MenuClient(18, connection);
                clientGUI.setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, 
                    "Erreur de connexion à la base de données: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}