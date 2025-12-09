import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenu extends JFrame {

    // --- Color Palette ---
    private final Color SIDEBAR_BG = new Color(40, 44, 52);
    private final Color MAIN_BG_1 = new Color(245, 247, 250);
    private final Color MAIN_BG_2 = new Color(230, 235, 240);
    private final Color TEXT_PRIMARY = new Color(60, 60, 60);
    private final Color ACCENT_COLOR = new Color(74, 144, 226);
    private final Color BTN_TEXT = new Color(220, 220, 220);
    
    // Chess/Checkers Theme Colors for Main Panel (Default Mockup Colors - White/Grey)
    private final Color BOARD_LIGHT = new Color(255, 255, 255, 100); 
    private final Color BOARD_DARK = new Color(150, 150, 150, 50);       
    private final Color PIECE_RED = new Color(200, 50, 50);
    private final Color PIECE_WHITE = new Color(240, 240, 240);
    private final Color RED_DARK = new Color(130, 20, 20); 
    private final Color WHITE_DARK = new Color(150, 150, 150);

    private JPanel difficultyMenuPanel;
    private JButton btnPlay;

    public MainMenu() {
        setTitle("Mak Hos Pro - Thai Checkers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 720);
        setLocationRelativeTo(null);

        JPanel container = new JPanel(new BorderLayout());
        setContentPane(container);

        // --- LEFT SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(280, 720));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(40, 20, 40, 20));

        // Logo
        JLabel logoLabel = new JLabel("MAK HOS");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel versionLabel = new JLabel("v 2.1 Pro Edition");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(150, 150, 160));
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logoLabel);
        sidebar.add(versionLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 60)));

        // 1.  PLAY
        btnPlay = createSidebarButton("▶  PLAY GAME", false);
        btnPlay.addActionListener(e -> toggleDifficultyMenu());
        sidebar.add(btnPlay);

        // 2.(Difficulty Buttons)
        difficultyMenuPanel = new JPanel();
        difficultyMenuPanel.setLayout(new BoxLayout(difficultyMenuPanel, BoxLayout.Y_AXIS));
        difficultyMenuPanel.setBackground(SIDEBAR_BG);
        difficultyMenuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        difficultyMenuPanel.setVisible(false); 

        // เพิ่มปุ่มย่อยลงในแผงเมนูย่อย (ย่อหน้าเข้าไปหน่อยให้ดูรู้ว่าเป็นลูกน้อง)
        difficultyMenuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        difficultyMenuPanel.add(createSubMenuButton("•  Easy Mode", 1));
        difficultyMenuPanel.add(createSubMenuButton("•  Normal Mode", 2));
        difficultyMenuPanel.add(createSubMenuButton("•  Hard Mode", 3));
        
        sidebar.add(difficultyMenuPanel);

        // ปุ่มอื่นๆ (ตัวอย่าง)
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        JButton btnSettings = createSidebarButton("⚙  SETTINGS", false);
        btnSettings.addActionListener(e -> {
            new SettingsMenu(this).setVisible(true); // ส่ง 'this' เพื่อให้ SettingsMenu รีเฟรชได้
        });
        sidebar.add(btnSettings);
        
        sidebar.add(Box.createVerticalGlue()); // ดัน Footer ลงล่างสุด
        
        JLabel creditLabel = new JLabel("© 2025 Java Project");
        creditLabel.setForeground(Color.GRAY);
        sidebar.add(creditLabel);

        container.add(sidebar, BorderLayout.WEST);

        // --- RIGHT MAIN AREA (Checkers Theme) ---
        MainContentPanel mainContent = new MainContentPanel();
        mainContent.setLayout(new GridBagLayout()); // ใช้ GridBagLayout เพื่อจัดกึ่งกลาง

        // 1. Title Container
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        JLabel welcomeTitle = new JLabel("MASTER THE KING PIECE");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 40));
        welcomeTitle.setForeground(TEXT_PRIMARY);
        welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel desc = new JLabel("Choose a difficulty level to challenge our Alpha-Zero AI.");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        desc.setForeground(Color.GRAY);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(welcomeTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(desc);
        
        // 2. Movement Display (แสดงทิศทางการเดิน)
        JPanel moveDisplay = new JPanel(new BorderLayout());
        moveDisplay.setOpaque(false);
        moveDisplay.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel whiteMove = new JLabel("AI (WHITE) Moves Downwards ▼", SwingConstants.CENTER);
        whiteMove.setFont(new Font("Segoe UI", Font.BOLD, 14));
        whiteMove.setForeground(new Color(120, 120, 120));
        
        JLabel redMove = new JLabel("PLAYER (RED) Moves Upwards ▲", SwingConstants.CENTER);
        redMove.setFont(new Font("Segoe UI", Font.BOLD, 14));
        redMove.setForeground(new Color(200, 50, 50));
        
        moveDisplay.add(whiteMove, BorderLayout.NORTH);
        moveDisplay.add(redMove, BorderLayout.SOUTH);
        
        // 3. Mockup Board/Pieces
        JPanel boardMockup = new CheckersMockupPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 30, 0); // Spacing below title
        
        // Layout components using GridBagLayout (Center alignment achieved by default)
        
        gbc.gridy = 0; // Title
        mainContent.add(titlePanel, gbc);
        
        gbc.gridy = 1; // Movement Info
        mainContent.add(moveDisplay, gbc);

        gbc.gridy = 2; // Board Mockup
        mainContent.add(boardMockup, gbc);
        
        container.add(mainContent, BorderLayout.CENTER);
    }
    
    // สร้างแผงจำลองกระดานหมากฮอสที่วาดด้วย Graphics2D
    private class CheckersMockupPanel extends JPanel {
    
        private final int TILE_SIZE = 80;
        private final int ROWS_COLS = 4;
        
        public CheckersMockupPanel() {
            setPreferredSize(new Dimension(ROWS_COLS * TILE_SIZE, ROWS_COLS * TILE_SIZE));
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // --- Define Theme Colors (ดึงค่าจาก CheckersBoard) ---
            Color lightTile, darkTile, redPieceColor, whitePieceColor, redDark, whiteDark;

            // ใช้ค่าจาก CheckersBoard.boardTheme
            if (CheckersBoard.boardTheme == 2) { // Dark Mode
                lightTile = new Color(70, 70, 70);
                darkTile = new Color(40, 40, 40);
                redPieceColor = new Color(255, 100, 100);
                whitePieceColor = new Color(200, 200, 200);
                redDark = new Color(130, 20, 20); 
                whiteDark = new Color(150, 150, 150);
            } else if (CheckersBoard.boardTheme == 3) { // Blue Ocean
                lightTile = new Color(173, 216, 230); 
                darkTile = new Color(70, 130, 180);  
                redPieceColor = new Color(200, 50, 50);
                whitePieceColor = new Color(255, 255, 255);
                redDark = new Color(150, 40, 40); 
                whiteDark = new Color(220, 220, 220);
            } else if (CheckersBoard.boardTheme == 4) { // Red Lava
                lightTile = new Color(255, 150, 150); 
                darkTile = new Color(180, 0, 0);     
                redPieceColor = new Color(40, 40, 40);    
                whitePieceColor = new Color(255, 255, 255);
                redDark = new Color(0, 0, 0); 
                whiteDark = new Color(200, 200, 200);
            } else if (CheckersBoard.boardTheme == 5) { // Marble/Gray
                lightTile = new Color(220, 220, 220); 
                darkTile = new Color(100, 100, 100);
                redPieceColor = new Color(180, 50, 50);
                whitePieceColor = new Color(255, 255, 255);
                redDark = new Color(100, 30, 30);
                whiteDark = new Color(180, 180, 180);
            } else if (CheckersBoard.boardTheme == 6) { // Neon Pink
                lightTile = new Color(255, 192, 203); 
                darkTile = new Color(255, 0, 127);
                redPieceColor = new Color(0, 0, 0);
                whitePieceColor = new Color(255, 255, 255);
                redDark = new Color(0, 0, 0);
                whiteDark = new Color(200, 200, 200);
            } else { // Default Theme (boardTheme == 1) - White/Grey Mockup
                lightTile = BOARD_LIGHT;
                darkTile = BOARD_DARK; 
                redPieceColor = PIECE_RED;
                whitePieceColor = PIECE_WHITE;
                redDark = RED_DARK;
                whiteDark = WHITE_DARK;
            }

            for (int r = 0; r < ROWS_COLS; r++) {
                for (int c = 0; c < ROWS_COLS; c++) { 
                    int x = c * TILE_SIZE;
                    int y = r * TILE_SIZE;
                    
                    // Draw Tile
                    g2.setColor((r + c) % 2 == 0 ? lightTile : darkTile);
                    g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                    
                    // Draw Piece on dark tiles
                    if ((r + c) % 2 != 0) {
                        
                        int pieceX = x + 10;
                        int pieceY = y + 10;
                        int pieceSize = TILE_SIZE - 20;

                        Color baseColor, highlightColor;
                        
                        // Assign colors
                        if (r < 2) { // White/AI pieces
                            baseColor = whitePieceColor;
                            highlightColor = whiteDark;
                        } else { // Red/Player pieces
                            baseColor = redPieceColor;
                            highlightColor = redDark;
                        }

                        // 1. Draw Shadow/Outline (for depth)
                        g2.setColor(Color.BLACK);
                        g2.fillOval(pieceX + 1, pieceY + 1, pieceSize, pieceSize);

                        // 2. Draw Main Piece based on CheckersBoard.pieceStyle
                        
                        int style = CheckersBoard.pieceStyle;
                        
                        if (style == 1 || style == 2 || style == 5) {
                            // Style 1 (Default), 2 (Dot), 5 (Glass) uses Oval/Gradient
                            GradientPaint gp;
                            
                            if (style == 5) { // Glass/Translucent
                                Color transparentBase = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 180);
                                Color transparentHighlight = new Color(highlightColor.getRed(), highlightColor.getGreen(), highlightColor.getBlue(), 120);
                                gp = new GradientPaint(pieceX, pieceY, transparentBase, 
                                                       pieceX + pieceSize, pieceY + pieceSize, transparentHighlight);
                            } else { // Default Gradient
                                gp = new GradientPaint(pieceX, pieceY, baseColor.brighter(), 
                                                       pieceX + pieceSize, pieceY + pieceSize, highlightColor);
                            }
                            g2.setPaint(gp);
                            g2.fillOval(pieceX, pieceY, pieceSize, pieceSize);
                            
                            if (style == 2) { // Dot effect
                                g2.setColor(Color.BLACK);
                                g2.drawOval(pieceX + pieceSize/4, pieceY + pieceSize/4, pieceSize/2, pieceSize/2);
                            }
                            
                        } else if (style == 3) {
                            // Style 3: Square
                            g2.setColor(baseColor);
                            g2.fillRect(pieceX, pieceY, pieceSize, pieceSize);
                            g2.setColor(Color.BLACK);
                            g2.drawRect(pieceX, pieceY, pieceSize, pieceSize);
                        } else if (style == 4 || style == 6) {
                            // Style 4: Classic (Outline) / Style 6: Monochromatic
                            Color outlineColor = (style == 6) ? baseColor.darker().darker().darker() : Color.BLACK;
                            Color mainColor = (style == 6) ? baseColor : baseColor;
                            
                            g2.setColor(outlineColor); 
                            g2.fillOval(pieceX - 2, pieceY - 2, pieceSize + 4, pieceSize + 4);
                            g2.setColor(mainColor);
                            g2.fillOval(pieceX, pieceY, pieceSize, pieceSize);
                        }
                        
                        // 3. Draw King Mark (Enhanced 3D Effect)
                        if (r == ROWS_COLS - 1 && c == 2) { 
                            int kingOutline = 2;
                            int kingRadius = pieceSize / 2;
                            int kingCenterX = pieceX + pieceSize / 2;
                            int kingCenterY = pieceY + pieceSize / 2;
                            
                            // 1. Shadow/Base Ring
                            g2.setColor(Color.BLACK);
                            g2.fillOval(kingCenterX - kingRadius / 2, kingCenterY - kingRadius / 2, kingRadius + 1, kingRadius + 1);

                            // 2. Inner King Shape (Gold Gradient)
                            Color kingColor = Color.YELLOW; 
                            Color kingDark = new Color(200, 150, 0); 
                            
                            GradientPaint gpKing = new GradientPaint(
                                kingCenterX - kingRadius / 2, kingCenterY - kingRadius / 2, kingColor.brighter(), 
                                kingCenterX + kingRadius / 2, kingCenterY + kingRadius / 2, kingDark
                            );
                            g2.setPaint(gpKing);
                            g2.fillOval(kingCenterX - kingRadius / 2 + kingOutline, 
                                        kingCenterY - kingRadius / 2 + kingOutline, 
                                        kingRadius - kingOutline * 2 + 1, 
                                        kingRadius - kingOutline * 2 + 1);

                            // 3. Draw a white highlight dot
                            g2.setColor(new Color(255, 255, 255, 180));
                            g2.fillOval(kingCenterX - kingRadius / 4, kingCenterY - kingRadius / 4, kingRadius / 4, kingRadius / 4);
                            
                            // 4. Draw small 'K' on top for clarity
                            g2.setColor(Color.BLACK); 
                            g2.setFont(new Font("Arial", Font.BOLD, 18));
                            FontMetrics fm = g2.getFontMetrics();
                            int tx = kingCenterX - fm.stringWidth("K") / 2;
                            int ty = kingCenterY - fm.getHeight() / 2 + fm.getAscent() - 2;
                            g2.drawString("K", tx, ty);
                        }
                    }
                }
            }
        }
    }

    // ฟังก์ชันสลับการแสดงผลเมนูย่อย
    private void toggleDifficultyMenu() {
        boolean isVisible = difficultyMenuPanel.isVisible();
        difficultyMenuPanel.setVisible(!isVisible);
        
        // เปลี่ยนไอคอนลูกศรให้ดูสมจริง
        if (!isVisible) {
            btnPlay.setText("▼  PLAY GAME");
            btnPlay.setBackground(new Color(50, 55, 65)); // เปลี่ยนสีค้างไว้ตอนเปิด
        } else {
            btnPlay.setText("▶  PLAY GAME");
            btnPlay.setBackground(SIDEBAR_BG);
        }
        
        // สั่งจัดหน้าจอใหม่ทันที (สำคัญมาก ไม่งั้นปุ่มจะไม่เลื่อน)
        revalidate();
        repaint();
    }

    // สร้างปุ่มเมนูหลัก
    private JButton createSidebarButton(String text, boolean isSubMenu) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(BTN_TEXT);
        btn.setBackground(SIDEBAR_BG);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(60, 65, 75));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // ถ้าเมนูเปิดอยู่ และเป็นปุ่ม Play ไม่ต้องคืนสีเดิม
                if (text.contains("PLAY GAME") && difficultyMenuPanel.isVisible()) return;
                btn.setBackground(SIDEBAR_BG);
            }
        });
        return btn;
    }

    // สร้างปุ่มเมนูย่อย
    private JButton createSubMenuButton(String text, int difficulty) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // ตัวบางลง
        btn.setForeground(new Color(180, 180, 180)); // สีจางลงนิดนึง
        btn.setBackground(SIDEBAR_BG);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 40, 8, 20)); // ย่อหน้าซ้ายเยอะหน่อย (40)
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(ACCENT_COLOR); // ชี้แล้วเป็นสีฟ้า
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(new Color(180, 180, 180));
            }
        });

        btn.addActionListener(e -> launchGameWindow(difficulty));
        return btn;
    }

    private void launchGameWindow(int difficulty) {
        this.dispose();
        JFrame gameFrame = new JFrame("Mak Hos - Gameplay");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game Option");
        JMenuItem itemBack = new JMenuItem("Main Menu");
        JMenuItem itemExit = new JMenuItem("Exit");
        
        itemBack.addActionListener(evt -> {
            gameFrame.dispose();
            new MainMenu().setVisible(true);
        });
        itemExit.addActionListener(evt -> System.exit(0));
        
        gameMenu.add(itemBack);
        gameMenu.addSeparator();
        gameMenu.add(itemExit);
        menuBar.add(gameMenu);
        gameFrame.setJMenuBar(menuBar);

        CheckersBoard board = new CheckersBoard(difficulty);
        gameFrame.add(board);
        
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        gameFrame.setResizable(false);
    }

    // Gradient Background
    private class MainContentPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth(), h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, MAIN_BG_1, w, h, MAIN_BG_2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }
}