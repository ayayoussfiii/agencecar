import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class InscriptionAPP extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(25, 55, 109);
    private static final Color SECONDARY_COLOR = new Color(45, 95, 155);
    private static final Color ACCENT_COLOR = new Color(240, 130, 50);
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    // Reduced width for text fields
    private static final int FIELD_WIDTH = 150; 

    private JTextField nomField, prenomField, loginField, numPermisField, telephoneField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JButton registerButton, homeButton;
    private JPanel resizeHandlePanel;

    public InscriptionAPP() {
        setTitle("AyaCar - Formulaire d'inscription");
        setSize(500, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Enable window resizing
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("INSCRIPTION", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Create fields with reduced width
        nomField = createStyledTextField(FIELD_WIDTH);
        prenomField = createStyledTextField(FIELD_WIDTH);
        loginField = createStyledTextField(FIELD_WIDTH);
        passwordField = createStyledPasswordField(FIELD_WIDTH);
        numPermisField = createStyledTextField(FIELD_WIDTH);
        telephoneField = createStyledTextField(FIELD_WIDTH);

        roleBox = new JComboBox<>(new String[]{"Client", "Admin"});
        roleBox.setFont(FIELD_FONT);
        roleBox.setBackground(Color.WHITE);
        roleBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        int row = 0;
        gbc.gridy = row++;
        formPanel.add(createFormLabel("Rôle:"), gbc);
        gbc.gridy = row++;
        formPanel.add(roleBox, gbc);

        gbc.gridy = row++;
        formPanel.add(createFormLabel("Nom:"), gbc);
        gbc.gridy = row++;
        formPanel.add(nomField, gbc);

        gbc.gridy = row++;
        formPanel.add(createFormLabel("Prénom:"), gbc);
        gbc.gridy = row++;
        formPanel.add(prenomField, gbc);

        gbc.gridy = row++;
        formPanel.add(createFormLabel("Login:"), gbc);
        gbc.gridy = row++;
        formPanel.add(loginField, gbc);

        gbc.gridy = row++;
        formPanel.add(createFormLabel("Mot de passe:"), gbc);
        gbc.gridy = row++;
        formPanel.add(passwordField, gbc);

        gbc.gridy = row++;
        formPanel.add(createFormLabel("Numéro de permis:"), gbc);
        gbc.gridy = row++;
        formPanel.add(numPermisField, gbc);

        gbc.gridy = row++;
        formPanel.add(createFormLabel("Téléphone:"), gbc);
        gbc.gridy = row++;
        formPanel.add(telephoneField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Register button
        registerButton = new JButton("S'inscrire");
        registerButton.setFont(BUTTON_FONT);
        registerButton.setBackground(SECONDARY_COLOR);
        registerButton.setForeground(Color.black);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(8, 25, 8, 25)
        ));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Home button
        homeButton = new JButton("Retour à l'accueil");
        homeButton.setFont(BUTTON_FONT);
        homeButton.setBackground(ACCENT_COLOR);
        homeButton.setForeground(Color.BLUE);
        homeButton.setFocusPainted(false);
        homeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(8, 25, 8, 25)
        ));
        homeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(homeButton);
        buttonPanel.add(registerButton);
        
        // Create resize handle panel
        resizeHandlePanel = new JPanel();
        resizeHandlePanel.setPreferredSize(new Dimension(15, 15));
        resizeHandlePanel.setBackground(SECONDARY_COLOR);
        resizeHandlePanel.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
        resizeHandlePanel.setToolTipText("Cliquer et glisser pour redimensionner");
        
        // Add a custom resize handle
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(resizeHandlePanel, BorderLayout.EAST);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add resize functionality to the handle
        ResizeListener resizeListener = new ResizeListener();
        resizeHandlePanel.addMouseListener(resizeListener);
        resizeHandlePanel.addMouseMotionListener(resizeListener);

        roleBox.addActionListener(e -> {
            String role = (String) roleBox.getSelectedItem();
            boolean isClient = role.equals("Client");
            nomField.setEnabled(isClient);
            prenomField.setEnabled(isClient);
            numPermisField.setEnabled(isClient);
            telephoneField.setEnabled(isClient);
        });

        registerButton.addActionListener(e -> registerUser());
        
        // Add action listener for home button
        homeButton.addActionListener(e -> {
            dispose(); // Close the current form
            // Here you would normally open your home screen
            // For example: new HomeScreen().setVisible(true);
        });
    }
    
    // Custom resize listener class
    private class ResizeListener extends MouseAdapter {
        private Point startPoint;
        
        @Override
        public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
            resizeHandlePanel.setBackground(ACCENT_COLOR); // Change color when pressed
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            if (startPoint != null) {
                Point currentPoint = e.getPoint();
                int dx = currentPoint.x - startPoint.x;
                int dy = currentPoint.y - startPoint.y;
                
                // Get current window size
                Dimension size = getSize();
                
                // Calculate new size
                int newWidth = size.width + dx;
                int newHeight = size.height + dy;
                
                // Set minimum size to avoid too small window
                newWidth = Math.max(newWidth, 400);
                newHeight = Math.max(newHeight, 450);
                
                // Resize the window
                setSize(newWidth, newHeight);
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            resizeHandlePanel.setBackground(SECONDARY_COLOR); // Restore original color
        }
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createStyledTextField(int width) {
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setPreferredSize(new Dimension(width, 28)); // Set fixed width
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField(int width) {
        JPasswordField field = new JPasswordField();
        field.setFont(FIELD_FONT);
        field.setPreferredSize(new Dimension(width, 28)); // Set fixed width
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private void registerUser() {
        String role = (String) roleBox.getSelectedItem();
        if (role.equals("Admin")) {
            String code = JOptionPane.showInputDialog(this, "Entrez le code secret pour les Admins :");
            if (code == null || !code.equals("ayaadmin")) {
                JOptionPane.showMessageDialog(this,
                        "Code incorrect. Inscription annulée.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());
        String numPermis = numPermisField.getText();
        String telephone = telephoneField.getText();
        String dateInscription = LocalDate.now().toString();

        String url = "jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);

            try {
                String sql;
                if (role.equals("Admin")) {
                    sql = "INSERT INTO Inscription (login, password, role) VALUES (?, ?, ?)";
                } else {
                    sql = "INSERT INTO Inscription (nom, prenom, login, password, num_permis, telephone, date_inscription, role) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                }

                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                if (role.equals("Admin")) {
                    stmt.setString(1, login);
                    stmt.setString(2, password);
                    stmt.setString(3, role);
                } else {
                    stmt.setString(1, nom);
                    stmt.setString(2, prenom);
                    stmt.setString(3, login);
                    stmt.setString(4, password);
                    stmt.setString(5, numPermis);
                    stmt.setString(6, telephone);
                    stmt.setString(7, dateInscription);
                    stmt.setString(8, role);
                }

                int rowsInserted = stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                int inscriptionId = 0;
                if (rs.next()) {
                    inscriptionId = rs.getInt(1);
                }

                if (role.equals("Admin")) {
                    sql = "INSERT INTO Users (id_Users, login, password, role) VALUES (?, ?, ?, ?)";
                    PreparedStatement userStmt = conn.prepareStatement(sql);
                    userStmt.setInt(1, inscriptionId);
                    userStmt.setString(2, login);
                    userStmt.setString(3, password);
                    userStmt.setString(4, role);
                    userStmt.executeUpdate();
                } else {
                    sql = "INSERT INTO Client (id, nom, prenom, telephone, num_permis, login) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement clientStmt = conn.prepareStatement(sql);
                    clientStmt.setInt(1, inscriptionId);
                    clientStmt.setString(2, nom);
                    clientStmt.setString(3, prenom);
                    clientStmt.setString(4, telephone);
                    clientStmt.setString(5, numPermis);
                    clientStmt.setString(6, login);
                    clientStmt.executeUpdate();
                }

                conn.commit();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this,
                            "<html><div style='width:200px;'>Inscription réussie !</div></html>",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "<html><div style='width:200px;color:red;'>Erreur lors de l'inscription : " +
                            ex.getMessage() + "</div></html>",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new InscriptionAPP().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}