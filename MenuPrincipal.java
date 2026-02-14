import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends JFrame {

    // Palette de couleurs
    private static final Color PRIMARY_COLOR = new Color(25, 55, 109);
    private static final Color SECONDARY_COLOR = new Color(45, 95, 155);
    private static final Color ACCENT_COLOR = new Color(240, 130, 50);
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 245);
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color TEXT_LIGHT = new Color(250, 250, 250);
    
    // Polices
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    private JPanel contentPanel;
    private List<JPanel> menuButtons = new ArrayList<>();
    private JPanel activeButton = null;

    public MenuPrincipal() {
        setTitle("AyaCar - Système de Gestion de Location");
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
        
        JLabel logoLabel = new JLabel("AYACAR");
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
                String text = "A";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.drawString(text, (getWidth() - textWidth)/2, (getHeight() + textHeight/2)/2 - 1);
                g2d.dispose();
            }
        };
        userIconPanel.setPreferredSize(new Dimension(28, 28));
        
        JLabel userLabel = new JLabel("Admin");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(TEXT_LIGHT);
        
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setBackground(ACCENT_COLOR);
        logoutButton.setBorder(new EmptyBorder(6, 12, 6, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            showWelcomePage();
            if (activeButton != null) {
                activeButton.setBackground(BACKGROUND_COLOR);
                Component[] components = activeButton.getComponents();
                for (Component c : components) {
                    if (c instanceof JLabel) {
                        ((JLabel) c).setForeground(TEXT_DARK);
                    }
                }
                activeButton = null;
            }
            JOptionPane.showMessageDialog(MenuPrincipal.this, 
                "Vous avez été déconnecté avec succès", 
                "Déconnexion", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
            SwingUtilities.invokeLater(() -> {
                new PageAccueil().setVisible(true);
            });
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

    private JPanel createSideMenuPanel() {
        JPanel sideMenu = new JPanel();
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));
        sideMenu.setBackground(BACKGROUND_COLOR);
        sideMenu.setPreferredSize(new Dimension(220, getHeight()));
        sideMenu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("AYACAR");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sideMenu.add(titleLabel);
        sideMenu.add(Box.createVerticalStrut(15));

        JLabel menuTitle = new JLabel("MENU PRINCIPAL");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        menuTitle.setForeground(PRIMARY_COLOR);
        menuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sideMenu.add(menuTitle);
        sideMenu.add(Box.createVerticalStrut(20));

        String[] menuOptions = {
        	    "1 - Gestion des Clients",
        	    "2 - Gestion des Véhicules", 
        	    "3 - Contrats de Location",
        	    "4 - Gestion des Réclamations",
        	    "5 - Gestion des Maintenances" 
        	};

        for (String option : menuOptions) {
            JPanel menuButton = createMenuButton(option);
            menuButtons.add(menuButton);
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
            public void mouseEntered(MouseEvent e) {
                textLabel.setForeground(ACCENT_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeButton) {
                    textLabel.setForeground(TEXT_DARK);
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveButton(button);
                handleMenuAction(text);
            }
        });
        
        return button;
    }

    private void setActiveButton(JPanel button) {
        if (activeButton != null) {
            activeButton.setBackground(BACKGROUND_COLOR);
            Component[] components = activeButton.getComponents();
            for (Component c : components) {
                if (c instanceof JLabel) {
                    ((JLabel) c).setForeground(TEXT_DARK);
                }
            }
        }
        
        activeButton = button;
        activeButton.setBackground(SECONDARY_COLOR);
        Component[] components = activeButton.getComponents();
        for (Component c : components) {
            if (c instanceof JLabel) {
                ((JLabel) c).setForeground(TEXT_LIGHT);
            }
        }
    }

    private void handleMenuAction(String action) {
        if (action.startsWith("1 - Gestion des Clients")) {
            SwingUtilities.invokeLater(() -> {
                ClientApp.displayClientsWithSearch();
            });
        } 
        else if (action.startsWith("2 - Gestion des Véhicules")) {
            SwingUtilities.invokeLater(() -> {
                VehiculeApp.displayVehiculesWithSearch(null);
            });
        }
        else if (action.startsWith("3 - Contrats de Location")) {
            SwingUtilities.invokeLater(() -> {
                LocationApp.displayLocations(this);
            });
        }
        else if (action.startsWith("4 - Gestion des Réclamations")) {
            SwingUtilities.invokeLater(() -> {
                ReclamationApp.displayReclamations(this);
            });
        }
            else if (action.startsWith("5 - Gestion des Maintenances")) {
                SwingUtilities.invokeLater(() -> {
                    // Obtenir l'ID de l'utilisateur admin actuellement connecté
                    String id_Users = "Admin"; 
                    MaintenanceStatusManager.displayMaintenances(this, id_Users);
                });
        }
    }

    private void showWelcomePage() {
        contentPanel.removeAll();
        contentPanel.add(createWelcomePanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        JPanel welcomeContent = new JPanel();
        welcomeContent.setLayout(new BoxLayout(welcomeContent, BoxLayout.Y_AXIS));
        welcomeContent.setBackground(Color.WHITE);
        welcomeContent.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    getWidth(), getHeight(), SECONDARY_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                
                g2d.setColor(new Color(255, 255, 255, 180));
                g2d.setStroke(new BasicStroke(3f));
                
                int carX = getWidth()/2 - 60;
                int carY = getHeight()/2 - 15;
                g2d.drawRoundRect(carX, carY, 120, 30, 10, 10);
                g2d.fillOval(carX + 20, carY + 25, 20, 20);
                g2d.fillOval(carX + 80, carY + 25, 20, 20);
                
                int[] xPoints = {carX + 30, carX + 50, carX + 80, carX + 100};
                int[] yPoints = {carY, carY - 20, carY - 20, carY};
                g2d.drawPolyline(xPoints, yPoints, 4);
                
                g2d.dispose();
            }
        };
        imagePanel.setPreferredSize(new Dimension(0, 180));
        imagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        imagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel welcomeLabel = new JLabel("Bienvenue dans AyaCar");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(PRIMARY_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Système de Gestion de Location de Voitures");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_DARK);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextArea descriptionArea = new JTextArea(
            "Sélectionnez une option dans le menu à gauche pour commencer à gérer " +
            "vos clients, véhicules, contrats de location et réclamations."
        );
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setForeground(new Color(80, 80, 80));
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        welcomeContent.add(imagePanel);
        welcomeContent.add(Box.createVerticalStrut(30));
        welcomeContent.add(welcomeLabel);
        welcomeContent.add(Box.createVerticalStrut(10));
        welcomeContent.add(subtitleLabel);
        welcomeContent.add(Box.createVerticalStrut(20));
        welcomeContent.add(descriptionArea);
        
        panel.setBorder(new CompoundBorder(
            new EmptyBorder(20, 20, 20, 20),
            BorderFactory.createCompoundBorder(
                new CustomShadowBorder(),
                BorderFactory.createLineBorder(new Color(230, 230, 230))
        )));
        
        panel.add(welcomeContent, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusBarPanel() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(245, 246, 247));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        statusBar.setPreferredSize(new Dimension(getWidth(), 25));
        
        JLabel statusLabel = new JLabel("© 2025 AyaCar - Système de gestion v1.0");
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

    class CustomShadowBorder extends EmptyBorder {
        public CustomShadowBorder() {
            super(5, 5, 5, 5);
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(0, 0, 0, 20));
            for (int i = 0; i < 5; i++) {
                g2d.drawRoundRect(x + i, y + i, width - i * 2, height - i * 2, 3, 3);
            }
            g2d.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new MenuPrincipal().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}