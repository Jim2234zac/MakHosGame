import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsMenu extends JFrame {

    private final Color PANEL_BG = new Color(245, 247, 250);
    private final Color TEXT_PRIMARY = new Color(60, 60, 60);
    private final Color ACCENT_COLOR = new Color(74, 144, 226);
    
    private MainMenu mainMenuInstance;

    public SettingsMenu(MainMenu menu) {
        this.mainMenuInstance = menu;
        
        setTitle("Mak Hos Pro - Settings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 480); 
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // --- Tabbed Pane ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        tabbedPane.addTab("🎨 Visual & Aids", createVisualSettingsPanel());
        tabbedPane.addTab("🧠 AI & Rules", createRulesSettingsPanel());
        tabbedPane.addTab("ℹ️ Other", createOtherSettingsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // --- Close Button ---
        JButton btnClose = new JButton("Save & Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setBackground(ACCENT_COLOR);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(btnClose);
        
        btnClose.addActionListener(e -> dispose());
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
    
    // --- Helper Method: Setting Panel ---
    private JPanel createSettingPanel(String labelText, JComponent component, String tooltip) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setPreferredSize(new Dimension(200, 25));
        
        if (tooltip != null) label.setToolTipText(tooltip);
        
        panel.add(label);
        panel.add(component);
        return panel;
    }
    
    // --- Helper Method: Title ---
    private JLabel createSectionTitle(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(TEXT_PRIMARY);
        label.setBorder(new EmptyBorder(10, 0, 10, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // =========================================================
    // 1. VISUAL SETTINGS TAB
    // =========================================================
    private JPanel createVisualSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        panel.add(createSectionTitle("Appearance (Live Preview)"));
        panel.add(createSettingPanel("Board Theme:", createBoardThemeChooser(), "Changes the color scheme of the board tiles."));
        panel.add(createSettingPanel("Piece Style:", createPieceStyleChooser(), "Changes the shape of the checker pieces."));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(createSectionTitle("Game Aids"));
        panel.add(createSettingPanel("Show Legal Moves:", createShowLegalMovesCheckbox(), "Highlights valid squares when a piece is selected."));
        
        panel.add(Box.createVerticalGlue()); 
        return panel;
    }

    private JComboBox<String> createBoardThemeChooser() {
        String[] themes = {
            "1. Classic Green", 
            "2. Dark Mode", 
            "3. Blue Ocean", 
            "4. Red Lava",
            "5. Marble/Gray",
            "6. Neon Pink"
        };
        JComboBox<String> comboBox = new JComboBox<>(themes);
        comboBox.setSelectedIndex(CheckersBoard.boardTheme - 1);
        comboBox.setPreferredSize(new Dimension(250, 30));

        comboBox.addActionListener(e -> {
            CheckersBoard.boardTheme = comboBox.getSelectedIndex() + 1;
            mainMenuInstance.repaint(); 
        });
        return comboBox;
    }

    private JComboBox<String> createPieceStyleChooser() {
        String[] styles = {
            "1. Default (Oval/Gradient)", 
            "2. Dot (Small Oval)", 
            "3. Square (Box)", 
            "4. Classic (Thick Outline)",
            "5. Glass/Translucent",
            "6. Monochromatic"
        };
        JComboBox<String> comboBox = new JComboBox<>(styles);
        comboBox.setSelectedIndex(CheckersBoard.pieceStyle - 1);
        comboBox.setPreferredSize(new Dimension(250, 30));
        
        comboBox.addActionListener(e -> {
            CheckersBoard.pieceStyle = comboBox.getSelectedIndex() + 1;
            mainMenuInstance.repaint(); 
        });
        return comboBox;
    }

    private JCheckBox createShowLegalMovesCheckbox() {
        JCheckBox cb = new JCheckBox();
        cb.setSelected(CheckersBoard.showLegalMoves);
        cb.addActionListener(e -> {
            CheckersBoard.showLegalMoves = cb.isSelected();
        });
        return cb;
    }


    // =========================================================
    // 2. AI & RULES SETTINGS TAB
    // =========================================================
    private JPanel createRulesSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        panel.add(createSectionTitle("AI Reaction Speed"));
        panel.add(createSettingPanel("AI Speed (Delay in ms):", createAIDelaySlider(), "Higher value means AI thinks slower."));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(createSectionTitle("Game Rules Variation"));
        panel.add(createSettingPanel("Force Capture Rule:", createForceCaptureCheckbox(), "If checked, a player must capture an enemy piece if possible."));
        panel.add(createSettingPanel("King Movement Rule:", createKingMoveRuleChooser(), "Choose between Long-Jump (Thai/Flying King) or Short-Jump (Standard Checkers)."));

        panel.add(Box.createVerticalGlue()); 
        return panel;
    }

    private JSlider createAIDelaySlider() {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 10, 500, CheckersBoard.aiDelay);
        slider.setMajorTickSpacing(200);
        slider.setMinorTickSpacing(50);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setPreferredSize(new Dimension(300, 50));
        
        slider.addChangeListener(e -> {
            CheckersBoard.aiDelay = slider.getValue();
        });
        return slider;
    }

    private JCheckBox createForceCaptureCheckbox() {
        JCheckBox cb = new JCheckBox();
        cb.setSelected(CheckersBoard.forceCapture);
        cb.addActionListener(e -> CheckersBoard.forceCapture = cb.isSelected());
        return cb;
    }
    
    private JComboBox<String> createKingMoveRuleChooser() {
        String[] rules = {"1. Flying King (Long Jump/Thai Rules)", "2. Short King (Standard Checkers/Max 2 Tiles)"};
        JComboBox<String> comboBox = new JComboBox<>(rules);
        comboBox.setSelectedIndex(CheckersBoard.kingMoveRule - 1);
        comboBox.setPreferredSize(new Dimension(300, 30));

        comboBox.addActionListener(e -> {
            CheckersBoard.kingMoveRule = comboBox.getSelectedIndex() + 1;
        });
        return comboBox;
    }
    
    // =========================================================
    // 3. OTHER SETTINGS TAB
    // =========================================================
    private JPanel createOtherSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        panel.add(createSectionTitle("Information"));
        
        JLabel version = new JLabel("Version: 2.1 Pro Edition");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        version.setBorder(new EmptyBorder(5, 0, 5, 0));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel project = new JLabel("Project: Java Swing Checkers (2025)");
        project.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        project.setBorder(new EmptyBorder(5, 0, 5, 0));
        project.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(version);
        panel.add(project);

        panel.add(Box.createVerticalGlue()); 
        return panel;
    }
}