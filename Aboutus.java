import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class Aboutus extends JFrame {

    // Couleurs basées sur le thème AYACAR
    private final Color PRIMARY_COLOR = new Color(0, 102, 204);      // Bleu AYACAR
    private final Color SECONDARY_COLOR = new Color(240, 240, 240);  // Gris clair
    private final Color ACCENT_COLOR = new Color(255, 153, 0);       // Orange pour les accents
    private final Color TEXT_COLOR = new Color(51, 51, 51);          // Texte foncé
    private final Color HEADER_BG = new Color(245, 245, 245);        // Fond du header

    // Polices modernes
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public Aboutus() {
        setTitle("AYACAR - À Propos");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal avec ombre portée
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(SECONDARY_COLOR);
        
        // Header avec logo et titre
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Contenu avec défilement
        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer avec bouton de retour
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel title = new JLabel("À Propos de AYACAR");
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR);
        
        JLabel subtitle = new JLabel("Système de Gestion de Location de Voitures");
        subtitle.setFont(SUBTITLE_FONT);
        subtitle.setForeground(TEXT_COLOR);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(HEADER_BG);
        textPanel.add(title);
        textPanel.add(subtitle);
        
        panel.add(textPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Section Notre Histoire
        panel.add(createSectionTitle("Notre Histoire"));
        panel.add(createSectionContent(
            "AYACAR a été fondée avec la vision de simplifier la location de véhicules pour tous. " +
            "Depuis nos débuts, nous nous engageons à fournir des solutions de mobilité fiables " +
            "et accessibles à travers notre plateforme de gestion complète."
        ));
        
        // Section Notre Mission
        panel.add(createSectionTitle("Notre Mission"));
        panel.add(createSectionContent(
            "Notre mission est de révolutionner l'expérience de location de voitures en offrant " +
            "une interface conviviale, des processus simplifiés et un service client exceptionnel " +
            "pour les gestionnaires et les clients."
        ));
        
        // Section Fonctionnalités
        panel.add(createSectionTitle("Fonctionnalités Clés"));
        panel.add(createFeaturesPanel());
        
        // Section Équipe
        panel.add(createSectionTitle("Notre Équipe"));
        panel.add(createSectionContent(
            "L'équipe AYACAR est composée de professionnels dévoués travaillant sans relâche " +
            "pour améliorer continuellement notre système et offrir la meilleure expérience " +
            "possible à nos utilisateurs."
        ));
        
        return panel;
    }
    
    private JPanel createSectionTitle(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 0, 5, 0));
        
        JLabel label = new JLabel(title);
        label.setFont(SECTION_FONT);
        label.setForeground(PRIMARY_COLOR);
        
        panel.add(label);
        return panel;
    }
    
    private JPanel createSectionContent(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 10, 15, 10));
        
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(BODY_FONT);
        textArea.setForeground(TEXT_COLOR);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(Color.WHITE);
        
        panel.add(textArea, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createFeaturesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 20, 10));
        
        panel.add(createFeatureCard("Gestion des Clients", "Gérez efficacement vos clients et leurs informations"));
        panel.add(createFeatureCard("Gestion des Véhicules", "Suivez et maintenez votre flotte de véhicules"));
        panel.add(createFeatureCard("Contrats de Location", "Créez et gérez les contrats de location facilement"));
        panel.add(createFeatureCard("Reporting", "Générez des rapports détaillés et analyses"));
        
        return panel;
    }
    
    private JPanel createFeatureCard(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 248, 248));
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(BODY_FONT);
        descArea.setForeground(TEXT_COLOR);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(new Color(248, 248, 248));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(descArea, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            new EmptyBorder(10, 20, 10, 20)
        ));
        
        // Date et info utilisateur (comme dans l'image)
        JLabel infoLabel = new JLabel("23 avril 2025 | Admin");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        
        // Bouton de retour
        JButton backButton = new JButton("Retour");
        styleButton(backButton, PRIMARY_COLOR, Color.BLACK);
        backButton.addActionListener(e -> this.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(HEADER_BG);
        buttonPanel.add(backButton);
        
        panel.add(infoLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(new CompoundBorder(
            new LineBorder(bgColor.darker(), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new Aboutus().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}