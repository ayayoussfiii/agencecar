import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.image.BufferedImage;

public class ReclamationApp {
    
    // Palette de couleurs
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(230, 126, 34);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_DARK = new Color(44, 62, 80);
    private static final Color TEXT_LIGHT = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(230, 232, 235);
    
    private static final String[] ETAT_OPTIONS = {"en cours", "traité"};
    private static final String DB_URL = "jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb";

    public static void displayReclamations(JFrame parent) {
        JFrame frame = new JFrame("Gestion des Réclamations");
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(parent);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel mainPanel = createShadowPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(CARD_COLOR);
        
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), new Color(25, 100, 160));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        headerPanel.setPreferredSize(new Dimension(900, 70));
        
        JLabel titleLabel = new JLabel("Gestion des Réclamations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_LIGHT);
        titleLabel.setIcon(createIcon());
        titleLabel.setIconTextGap(15);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(CARD_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
        
        JLabel searchSectionLabel = new JLabel("Recherche et filtres");
        searchSectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchSectionLabel.setForeground(TEXT_DARK);
        searchSectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchPanel.add(searchSectionLabel);
        searchPanel.add(Box.createVerticalStrut(15));
        
        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchControlsPanel.setBackground(CARD_COLOR);
        searchControlsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(220, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JComboBox<String> filterBox = new JComboBox<>(new String[]{"Tous", "en cours", "traité"});
        filterBox.setPreferredSize(new Dimension(150, 35));
        filterBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterBox.setBackground(CARD_COLOR);
        
        JButton searchButton = createStyledButton("Rechercher", ACCENT_COLOR);
        JButton refreshButton = createStyledButton("Actualiser", PRIMARY_COLOR);
        
        JLabel searchLabel = new JLabel("Recherche:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_DARK);
        
        JLabel stateLabel = new JLabel("État:");
        stateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stateLabel.setForeground(TEXT_DARK);
        
        searchControlsPanel.add(searchLabel);
        searchControlsPanel.add(searchField);
        searchControlsPanel.add(stateLabel);
        searchControlsPanel.add(filterBox);
        searchControlsPanel.add(searchButton);
        searchControlsPanel.add(refreshButton);
        
        searchPanel.add(searchControlsPanel);
        searchPanel.add(Box.createVerticalStrut(20));
        
        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER_COLOR);
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchPanel.add(separator);
        
        JPanel tableContainerPanel = new JPanel(new BorderLayout());
        tableContainerPanel.setBackground(CARD_COLOR);
        tableContainerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 20, 25));
        
        JLabel tableSectionLabel = new JLabel("Liste des réclamations");
        tableSectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableSectionLabel.setForeground(TEXT_DARK);
        tableContainerPanel.add(tableSectionLabel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "ID Client", "Date", "Sujet", "Message", "État", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(230, 236, 245));
        table.setSelectionForeground(TEXT_DARK);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(240, 242, 245));
        header.setForeground(TEXT_DARK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        
        int[] columnWidths = {60, 100, 150, 200, 250, 100, 120};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        
        table.getColumnModel().getColumn(6).setCellRenderer(new ModernButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ModernButtonEditor(new JTextField()));
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = (JLabel) c;
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                
                if (!isSelected) {
                    label.setBackground(row % 2 == 0 ? CARD_COLOR : new Color(249, 250, 252));
                }
                
                if (column == 5) {
                    String etat = value.toString();
                    if ("en cours".equals(etat)) {
                        label.setForeground(new Color(243, 156, 18));
                    } else if ("traité".equals(etat)) {
                        label.setForeground(new Color(46, 204, 113));
                    }
                    label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else {
                    label.setForeground(TEXT_DARK);
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_COLOR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                int shadowSize = 3;
                for (int i = 0; i < shadowSize; i++) {
                    float opacity = 0.1f - (i * 0.03f);
                    g2.setColor(new Color(0, 0, 0, Math.max(0, (int)(opacity * 255))));
                    g2.drawLine(0, getHeight() - i, getWidth(), getHeight() - i);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setUI(new BasicScrollBarUI() {
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(200, 200, 200);
                this.trackColor = CARD_COLOR;
            }
            
            protected JButton createDecreaseButton(int orientation) {
                return createEmptyButton();
            }
            
            protected JButton createIncreaseButton(int orientation) {
                return createEmptyButton();
            }
            
            private JButton createEmptyButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        
        tableContainerPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(CARD_COLOR);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        JLabel countLabel = new JLabel("0 réclamation(s) trouvée(s)");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countLabel.setForeground(TEXT_DARK);
        statusPanel.add(countLabel, BorderLayout.WEST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(searchPanel, BorderLayout.PAGE_START);
        mainPanel.add(tableContainerPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        
        frame.setContentPane(contentPanel);
        
        loadReclamations(model, countLabel, null);
        
        refreshButton.addActionListener(e -> loadReclamations(model, countLabel, null));
        
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            String filterState = filterBox.getSelectedIndex() == 0 ? null : (String) filterBox.getSelectedItem();
            loadReclamations(model, countLabel, new ReclamationFilter(searchTerm, filterState));
        });
        
        frame.setVisible(true);
    }
    
    private static JPanel createShadowPanel() {
        JPanel shadowPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                int shadowSize = 5;
                for (int i = 0; i < shadowSize; i++) {
                    float opacity = 0.15f - (i * 0.03f);
                    g2d.setColor(new Color(0, 0, 0, Math.max(0, (int)(opacity * 255))));
                    g2d.setStroke(new BasicStroke(1.0f));
                    g2d.drawRoundRect(i, i, getWidth() - (i * 2) - 1, getHeight() - (i * 2) - 1, 12, 12);
                }
                
                g2d.dispose();
            }
        };
        shadowPanel.setOpaque(false);
        return shadowPanel;
    }
    
    private static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(TEXT_LIGHT);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 35));
        
        return button;
    }
    
    private static Icon createIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(ACCENT_COLOR);
                g2d.fillOval(x, y, 24, 24);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x + 7, y + 6, 10, 12);
                g2d.drawLine(x + 9, y + 10, x + 15, y + 10);
                g2d.drawLine(x + 9, y + 12, x + 15, y + 12);
                g2d.drawLine(x + 9, y + 14, x + 13, y + 14);
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return 24;
            }
            
            @Override
            public int getIconHeight() {
                return 24;
            }
        };
    }
    
    private static void loadReclamations(DefaultTableModel model, JLabel countLabel, ReclamationFilter filter) {
        model.setRowCount(0);
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            
            StringBuilder sql = new StringBuilder("SELECT id_Reclamation, id, date_Reclamation, Sujet, Message, Etat FROM Reclamation");
            
            if (filter != null) {
                boolean hasCondition = false;
                
                if (filter.getSearchTerm() != null && !filter.getSearchTerm().isEmpty()) {
                    sql.append(" WHERE (Sujet LIKE ? OR Message LIKE ? OR id LIKE ?)");
                    hasCondition = true;
                }
                
                if (filter.getEtat() != null) {
                    if (hasCondition) {
                        sql.append(" AND");
                    } else {
                        sql.append(" WHERE");
                    }
                    sql.append(" Etat = ?");
                }
            }
            
            sql.append(" ORDER BY date_Reclamation DESC");
            
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            
            int paramIndex = 1;
            if (filter != null && filter.getSearchTerm() != null && !filter.getSearchTerm().isEmpty()) {
                String searchParam = "%" + filter.getSearchTerm() + "%";
                pstmt.setString(paramIndex++, searchParam);
                pstmt.setString(paramIndex++, searchParam);
                pstmt.setString(paramIndex++, searchParam);
            }
            
            if (filter != null && filter.getEtat() != null) {
                pstmt.setString(paramIndex, filter.getEtat());
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            int count = 0;
            
            while (rs.next()) {
                Integer idReclamation = rs.getInt("id_Reclamation");
                String idClient = rs.getString("id");
                Date dateReclamation = new Date(rs.getTimestamp("date_Reclamation").getTime());
                String sujet = rs.getString("Sujet");
                String message = rs.getString("Message");
                String etat = rs.getString("Etat");
                
                String shortMessage = message.length() > 30 ? message.substring(0, 30) + "..." : message;
                
                model.addRow(new Object[]{
                    idReclamation,
                    idClient,
                    dateFormat.format(dateReclamation),
                    sujet,
                    shortMessage,
                    etat,
                    "Modifier état"
                });
                
                count++;
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
            countLabel.setText(count + " réclamation(s) trouvée(s)");
            
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Erreur lors du chargement des réclamations", e.getMessage());
        }
    }
    
    private static void showErrorDialog(String title, String message) {
        JDialog dialog = new JDialog((Frame) null, title, true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        panel.add(iconLabel, BorderLayout.WEST);
        
        JLabel messageLabel = new JLabel("<html><body><p style='width: 250px;'>" + message + "</p></body></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(messageLabel, BorderLayout.CENTER);
        
        JButton okButton = createStyledButton("OK", PRIMARY_COLOR);
        okButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
    
    public static ReclamationData chargerReclamation(int idReclamation) throws SQLException {
        String sql = "SELECT id_Reclamation, id, date_Reclamation, Sujet, Message, Etat FROM Reclamation WHERE id_Reclamation = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idReclamation);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                ReclamationData reclamation = new ReclamationData();
                reclamation.idReclamation = rs.getInt("id_Reclamation");
                reclamation.idClient = rs.getString("id");
                reclamation.dateReclamation = new Date(rs.getTimestamp("date_Reclamation").getTime());
                reclamation.sujet = rs.getString("Sujet");
                reclamation.message = rs.getString("Message");
                reclamation.etat = rs.getString("Etat");
                return reclamation;
            }
            return null;
        }
    }
    
    public static void mettreAJourEtatReclamation(int idReclamation, String nouvelEtat) throws SQLException {
        if (!nouvelEtat.equals("en cours") && !nouvelEtat.equals("traité")) {
            throw new IllegalArgumentException("L'état doit être 'en cours' ou 'traité'");
        }
        
        String sql = "UPDATE Reclamation SET Etat = ? WHERE id_Reclamation = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nouvelEtat);
            pstmt.setInt(2, idReclamation);
            pstmt.executeUpdate();
        }
    }
    
    private static void showSuccessNotification(String message) {
        JDialog notification = new JDialog((Frame) null, "Succès", true);
        notification.setSize(350, 150);
        notification.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(46, 204, 113));
        
        JLabel iconLabel = new JLabel(new ImageIcon(createCheckmarkImage()));
        panel.add(iconLabel, BorderLayout.WEST);
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageLabel.setForeground(Color.WHITE);
        panel.add(messageLabel, BorderLayout.CENTER);
        
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        okButton.setBackground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setPreferredSize(new Dimension(80, 30));
        okButton.addActionListener(e -> notification.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        notification.setContentPane(panel);
        notification.setVisible(true);
    }

    private static BufferedImage createCheckmarkImage() {
        BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.WHITE);
        g2d.fillOval(0, 0, 30, 30);
        
        g2d.setColor(new Color(46, 204, 113));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(8, 15, 13, 20);
        g2d.drawLine(13, 20, 22, 10);
        
        g2d.dispose();
        return img;
    }

    private static class ReclamationData {
        public Integer idReclamation;
        public String idClient;
        public Date dateReclamation;
        public String sujet;
        public String message;
        public String etat;
    }

    private static class ReclamationFilter {
        private String searchTerm;
        private String etat;
        
        public ReclamationFilter(String searchTerm, String etat) {
            this.searchTerm = searchTerm;
            this.etat = etat;
        }
        
        public String getSearchTerm() { return searchTerm; }
        public String getEtat() { return etat; }
    }

    private static class ModernButtonRenderer extends JButton implements TableCellRenderer {
        public ModernButtonRenderer() {
            setOpaque(true);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(TEXT_LIGHT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            setBackground(SECONDARY_COLOR);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return this;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    private static class ModernButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int selectedRow;
        private JTable table;
        
        public ModernButtonEditor(JTextField textField) {
            super(textField);
            
            button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    if (getModel().isPressed()) {
                        g2d.setColor(SECONDARY_COLOR.darker());
                    } else if (getModel().isRollover()) {
                        g2d.setColor(SECONDARY_COLOR.brighter());
                    } else {
                        g2d.setColor(SECONDARY_COLOR);
                    }
                    
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };
            
            button.setOpaque(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setForeground(TEXT_LIGHT);
            button.setContentAreaFilled(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            button.addActionListener(e -> {
                clicked = true;
                fireEditingStopped();
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.label = value.toString();
            this.selectedRow = row;
            this.table = table;
            button.setText(label);
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                Integer idReclamation = (Integer) table.getValueAt(selectedRow, 0);
                String currentEtat = (String) table.getValueAt(selectedRow, 5);
                
                try {
                    ReclamationData reclamation = chargerReclamation(idReclamation);
                    if (reclamation != null) {
                        showStateChangeDialog(idReclamation, currentEtat);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showErrorDialog("Erreur", "Erreur lors de la modification de l'état : " + ex.getMessage());
                }
                
                clicked = false;
            }
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
        
        private void showStateChangeDialog(int idReclamation, String currentEtat) {
            JDialog dialog = new JDialog((Frame) null, "Modification d'état", true);
            dialog.setSize(400, 250);
            dialog.setLocationRelativeTo(null);
            dialog.setResizable(false);
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.WHITE);
            
            JPanel headerPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), new Color(25, 100, 160));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                }
            };
            headerPanel.setLayout(new BorderLayout());
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            headerPanel.setPreferredSize(new Dimension(400, 60));
            
            JLabel titleLabel = new JLabel("Modifier l'état de la réclamation #" + idReclamation);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel, BorderLayout.CENTER);
            
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel stateLabel = new JLabel("Sélectionnez le nouvel état :");
            stateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            stateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(stateLabel);
            contentPanel.add(Box.createVerticalStrut(15));
            
            JPanel radioPanel = new JPanel(new GridLayout(1, 2, 10, 0));
            radioPanel.setBackground(Color.WHITE);
            radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            ButtonGroup buttonGroup = new ButtonGroup();
            JRadioButton enCoursRadio = new JRadioButton("En cours");
            JRadioButton traiteRadio = new JRadioButton("Traité");
            
            enCoursRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            traiteRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            enCoursRadio.setBackground(Color.WHITE);
            traiteRadio.setBackground(Color.WHITE);
            
            if ("en cours".equals(currentEtat)) {
                enCoursRadio.setSelected(true);
            } else {
                traiteRadio.setSelected(true);
            }
            
            buttonGroup.add(enCoursRadio);
            buttonGroup.add(traiteRadio);
            radioPanel.add(enCoursRadio);
            radioPanel.add(traiteRadio);
            contentPanel.add(radioPanel);
            contentPanel.add(Box.createVerticalStrut(25));
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JButton cancelButton = new JButton("Annuler");
            cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            cancelButton.setForeground(TEXT_DARK);
            cancelButton.setBackground(new Color(230, 230, 230));
            cancelButton.setFocusPainted(false);
            cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cancelButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
            
            JButton saveButton = createStyledButton("Enregistrer", ACCENT_COLOR);
            saveButton.setPreferredSize(new Dimension(120, 36));
            
            buttonPanel.add(cancelButton);
            buttonPanel.add(Box.createHorizontalStrut(10));
            buttonPanel.add(saveButton);
            contentPanel.add(buttonPanel);
            
            panel.add(headerPanel, BorderLayout.NORTH);
            panel.add(contentPanel, BorderLayout.CENTER);
            dialog.setContentPane(panel);
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            saveButton.addActionListener(e -> {
                String nouvelEtat = enCoursRadio.isSelected() ? "en cours" : "traité";
                
                if (!nouvelEtat.equals(currentEtat)) {
                    try {
                        mettreAJourEtatReclamation(idReclamation, nouvelEtat);
                        table.setValueAt(nouvelEtat, selectedRow, 5);
                        showSuccessNotification("L'état de la réclamation a été modifié avec succès");
                        dialog.dispose();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showErrorDialog("Erreur", "Erreur lors de la mise à jour : " + ex.getMessage());
                    }
                } else {
                    dialog.dispose();
                }
            });
            
            dialog.setVisible(true);
        }
    }
}