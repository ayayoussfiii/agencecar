import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PageAccueil extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(0, 90, 150);
    private static final Color SECONDARY_COLOR = new Color(245, 247, 250);
    private static final Color ACCENT_COLOR = new Color(0, 150, 215);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BUTTON_COLOR = new Color(0, 120, 180);
    
    // Code secret pour la réinitialisation du mot de passe
    private static final String ADMIN_SECRET_CODE = "ayaadmin";

    public PageAccueil() {
        setTitle("AyaCar - Location de Véhicules");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(SECONDARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        add(mainPanel);

        mainPanel.add(createLogoPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(createAuthPanel());
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(createFooterPanel());

        setVisible(true);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(PRIMARY_COLOR);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel logoLabel = new JLabel("AyaCar");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 32));
        logoLabel.setForeground(Color.WHITE);

        JLabel sloganLabel = new JLabel(" • Louez vite, roulez loin !");
        sloganLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        sloganLabel.setForeground(Color.WHITE);

        logoPanel.add(logoLabel);
        logoPanel.add(sloganLabel);
        return logoPanel;
    }

    private JPanel createAuthPanel() {
        JPanel authPanel = new JPanel();
        authPanel.setLayout(new BoxLayout(authPanel, BoxLayout.Y_AXIS));
        authPanel.setBackground(Color.WHITE);
        authPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));
        authPanel.setMaximumSize(new Dimension(450, 400));

        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Connectez-vous pour accéder à nos services");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setMaximumSize(new Dimension(400, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.weightx = 1.0;

        JLabel usernameLabel = new JLabel("Nom d'utilisateur");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Mot de passe");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(TEXT_COLOR);
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);

        JLabel roleLabel = new JLabel("Rôle");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setForeground(TEXT_COLOR);
        gbc.gridy = 4;
        formPanel.add(roleLabel, gbc);

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Client", "Admin"});
        roleBox.setFont(new Font("Arial", Font.PLAIN, 14));
        roleBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridy = 5;
        formPanel.add(roleBox, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(400, 45));
        JButton loginBtn = createStyledButton("Se connecter", BUTTON_COLOR);
        JButton signUpBtn = createStyledButton("S'inscrire", new Color(100, 100, 100));
        buttonPanel.add(loginBtn);
        buttonPanel.add(signUpBtn);

        JLabel forgotPasswordLabel = new JLabel("Mot de passe oublié ?");
        forgotPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPasswordLabel.setForeground(ACCENT_COLOR);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        authPanel.add(titleLabel);
        authPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        authPanel.add(subtitleLabel);
        authPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        authPanel.add(formPanel);
        authPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        authPanel.add(buttonPanel);
        authPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        authPanel.add(forgotPasswordLabel);

        // Actions
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String selectedRole = (String) roleBox.getSelectedItem();
            String role = selectedRole.equalsIgnoreCase("Admin") ? "admin" : "client";

            try {
                Connection conn = getDatabaseConnection();
                int userId = authenticateUser(conn, username, password, role);
                
                if (userId != -1) {
                    JOptionPane.showMessageDialog(this, "Connexion réussie !", "Bienvenue", JOptionPane.INFORMATION_MESSAGE);
                    
                    if (role.equals("admin")) {
                        SwingUtilities.invokeLater(() -> {
                            new MenuPrincipal().setVisible(true);
                            dispose();
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            new MenuClient(userId, conn).setVisible(true);
                            dispose();
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur de connexion: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        signUpBtn.addActionListener(e -> {
            new InscriptionAPP().setVisible(true);
            this.dispose();
        });

        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showPasswordResetDialog();
            }
        });

        return authPanel;
    }
    
    // Nouvelle méthode pour gérer la réinitialisation du mot de passe
    private void showPasswordResetDialog() {
        // Première étape: vérification du code secret admin
        String secretCode = JOptionPane.showInputDialog(this, 
            "Veuillez saisir le code secret admin pour continuer:", 
            "Réinitialisation du mot de passe", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (secretCode == null) {
            return; // L'utilisateur a annulé
        }
        
        if (!secretCode.equals(ADMIN_SECRET_CODE)) {
            JOptionPane.showMessageDialog(this, 
                "Code secret incorrect.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Deuxième étape: demande du nom d'utilisateur
        String username = JOptionPane.showInputDialog(this, 
            "Entrez votre nom d'utilisateur:", 
            "Réinitialisation du mot de passe", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (username == null || username.trim().isEmpty()) {
            return; // L'utilisateur a annulé ou n'a pas entré de nom d'utilisateur
        }
        
        // Vérifier si l'utilisateur existe
        try {
            Connection conn = getDatabaseConnection();
            if (!userExists(conn, username)) {
                JOptionPane.showMessageDialog(this, 
                    "Cet utilisateur n'existe pas.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Troisième étape: saisie du nouveau mot de passe
            JPasswordField newPassField = new JPasswordField(15);
            JPasswordField confirmPassField = new JPasswordField(15);
            
            JPanel passPanel = new JPanel(new GridLayout(4, 1, 5, 5));
            passPanel.add(new JLabel("Nouveau mot de passe:"));
            passPanel.add(newPassField);
            passPanel.add(new JLabel("Confirmer le mot de passe:"));
            passPanel.add(confirmPassField);
            
            int result = JOptionPane.showConfirmDialog(this, 
                passPanel, 
                "Réinitialisation du mot de passe", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);
                
            if (result != JOptionPane.OK_OPTION) {
                return; // L'utilisateur a annulé
            }
            
            String newPassword = new String(newPassField.getPassword());
            String confirmPassword = new String(confirmPassField.getPassword());
            
            if (newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, 
                    "Les mots de passe ne correspondent pas ou sont vides.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Mettre à jour le mot de passe dans la base de données
            if (updatePassword(conn, username, newPassword)) {
                JOptionPane.showMessageDialog(this, 
                    "Mot de passe mis à jour avec succès.", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Échec de la mise à jour du mot de passe.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
            conn.close();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la réinitialisation du mot de passe: " + ex.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // Vérifier si l'utilisateur existe dans la base de données
    private boolean userExists(Connection conn, String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM Inscription WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
    
    // Mettre à jour le mot de passe dans la base de données
    private boolean updatePassword(Connection conn, String username, String newPassword) throws SQLException {
        String query = "UPDATE Inscription SET password = ? WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private Connection getDatabaseConnection() throws SQLException, ClassNotFoundException {
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        String connectionString = "jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb";
        return DriverManager.getConnection(connectionString);
    }

    private int authenticateUser(Connection conn, String username, String password, String role) throws SQLException {
        String query = "SELECT id FROM Inscription WHERE login = ? AND password = ? AND role = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
        }
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(SECONDARY_COLOR);
        footerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setForeground(new Color(200, 200, 200));
        footerPanel.add(separator);
        footerPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(SECONDARY_COLOR);

        String[] links = {"Mentions légales", "CGU", "Politique de confidentialité", "Contact"};
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 5, 5);
        for (int i = 0; i < links.length; i++) {
            gbc.gridx = i;
            JLabel link = new JLabel(links[i]);
            link.setForeground(ACCENT_COLOR);
            link.setFont(new Font("Arial", Font.PLAIN, 12));
            link.setCursor(new Cursor(Cursor.HAND_CURSOR));
            final String linkName = links[i];
            link.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    JOptionPane.showMessageDialog(PageAccueil.this, 
                        "Page " + linkName + " à venir.", 
                        "Information", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            });
            contentPanel.add(link, gbc);
        }

        footerPanel.add(contentPanel);
        return footerPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new PageAccueil().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}