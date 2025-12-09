import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CheckersBoard.java
 * Fixed: Win Condition (No moves left = Loss) and Flying King logic.
 * UPDATE: Non-King pieces (including AI) can only move/capture forward.
 * ADDED: Static variables for Piece Style, Board Theme, and AI Delay, and multiple rules options.
 * FIX: Improved visibility of valid move highlights for all themes.
 */
public class CheckersBoard extends JPanel {

    // Constants
    private final int TILE_SIZE = 80;
    private final int ROWS = 8;
    private final int COLS = 8;
    
    // Game State
    private Piece[][] board;
    private int currentPlayer = Piece.RED; // RED starts
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean gameover = false;
    private String gameStatus = "Your Turn (Red)";
    
    // --- SETTINGS (Static variables for theme/delay/rules) ---
    public static int pieceStyle = 1; // 1=Default Oval, 2=Dot, 3=Square, 4=Classic (Outline), 5=Glass/Translucent, 6=Monochromatic
    public static int boardTheme = 1; // 1=Classic Green (Default), 2=Dark Mode, 3=Blue Ocean, 4=Red Lava, 5=Marble/Gray, 6=Neon Pink
    public static int aiDelay = 100; // Delay in milliseconds (Default 100)
    public static boolean showLegalMoves = true; // Show visual aid for moves
    public static boolean forceCapture = true; // Enforce mandatory capture
    public static boolean enableSound = true; // Global sound control
    public static float soundVolume = 0.8f; // Sound volume 
    public static int kingMoveRule = 1; // 1=Flying King, 2=Short King

    // AI Difficulty (1=Easy, 2=Normal, 3=Hard)
    private int difficultyLevel;

    public CheckersBoard(int difficulty) {
        this.difficultyLevel = difficulty;
        setPreferredSize(new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE));
        board = new Piece[ROWS][COLS];
        initBoard();
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentPlayer == Piece.RED && !gameover) {
                    handleMouseClick(e.getX(), e.getY());
                }
            }
        });
    }

    private void initBoard() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if ((r + c) % 2 != 0) {
                    if (r < 2) board[r][c] = new Piece(Piece.WHITE); // AI
                    else if (r > 5) board[r][c] = new Piece(Piece.RED); // Player
                }
            }
        }
    }

    private void handleMouseClick(int x, int y) {
        int c = x / TILE_SIZE;
        int r = y / TILE_SIZE;

        if (r < 0 || r >= ROWS || c < 0 || c >= COLS) return;

        if (selectedRow == -1) {
            // Select a piece
            if (board[r][c] != null && board[r][c].getColor() == currentPlayer) {
                selectedRow = r;
                selectedCol = c;
            }
        } else {
            // Move piece
            if (isValidMove(board, selectedRow, selectedCol, r, c)) {
                executeMove(board, selectedRow, selectedCol, r, c);
                selectedRow = -1;
                selectedCol = -1;
                
                checkWinCondition(); // Check immediately after player move
                
                if (!gameover) {
                    currentPlayer = Piece.WHITE;
                    gameStatus = "AI is thinking...";
                    repaint();
                    
                    // Small delay for AI (ใช้ค่า aiDelay ที่ตั้งค่าไว้)
                    Timer timer = new Timer(aiDelay, evt -> {
                        aiMove();
                        checkWinCondition(); // Check immediately after AI move
                        if (!gameover) {
                            currentPlayer = Piece.RED;
                            gameStatus = "Your Turn (Red)";
                        }
                        repaint();
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            } else {
                // Change selection
                if (board[r][c] != null && board[r][c].getColor() == currentPlayer) {
                    selectedRow = r;
                    selectedCol = c;
                } else {
                    selectedRow = -1;
                    selectedCol = -1;
                }
            }
        }
        repaint();
    }

    // --- AI MAIN LOGIC ---
    private void aiMove() {
        if (difficultyLevel == 1) {
            makeRandomMove(); 
        } else if (difficultyLevel == 2) {
            makeHeuristicMove(); 
        } else {
            makeMinimaxMove(); 
        }
    }

    private void makeRandomMove() {
        List<int[]> moves = getAllLegalMoves(board, Piece.WHITE);
        if (!moves.isEmpty()) {
            int[] m = moves.get(new Random().nextInt(moves.size()));
            executeMove(board, m[0], m[1], m[2], m[3]);
        }
    }

    private void makeHeuristicMove() {
        List<int[]> moves = getAllLegalMoves(board, Piece.WHITE);
        List<int[]> captures = new ArrayList<>();
        for (int[] m : moves) {
            if (Math.abs(m[2] - m[0]) >= 2) captures.add(m);
        }
        
        if (!captures.isEmpty()) {
            int[] m = captures.get(new Random().nextInt(captures.size()));
            executeMove(board, m[0], m[1], m[2], m[3]);
        } else if (!moves.isEmpty()) {
            int[] m = moves.get(new Random().nextInt(moves.size()));
            executeMove(board, m[0], m[1], m[2], m[3]);
        }
    }

    private void makeMinimaxMove() {
        int depth = 3; 
        int[] bestMove = null;
        int maxEval = Integer.MIN_VALUE;
        
        List<int[]> moves = getAllLegalMoves(board, Piece.WHITE);
        
        if (forceCapture) { 
            List<int[]> captures = new ArrayList<>();
            for (int[] m : moves) if (Math.abs(m[2] - m[0]) >= 2) captures.add(m);
            if (!captures.isEmpty()) moves = captures; 
        }

        for (int[] move : moves) {
            Piece[][] tempBoard = cloneBoard(board);
            executeMove(tempBoard, move[0], move[1], move[2], move[3]);
            
            int eval = minimax(tempBoard, depth - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            
            if (eval > maxEval) {
                maxEval = eval;
                bestMove = move;
            }
        }

        if (bestMove != null) {
            executeMove(board, bestMove[0], bestMove[1], bestMove[2], bestMove[3]);
        } else {
            makeRandomMove(); 
        }
    }

    private int minimax(Piece[][] currentBoard, int depth, boolean isMaximizing, int alpha, int beta) {
        if (depth == 0) {
            return evaluateBoard(currentBoard);
        }

        int color = isMaximizing ? Piece.WHITE : Piece.RED;
        List<int[]> moves = getAllLegalMoves(currentBoard, color);
        
        // Force capture in Minimax (Uses global setting)
        if (forceCapture) { 
            List<int[]> captures = new ArrayList<>();
            for (int[] m : moves) if (Math.abs(m[2] - m[0]) >= 2) captures.add(m);
            if (!captures.isEmpty()) moves = captures;
        }

        if (moves.isEmpty()) return evaluateBoard(currentBoard);

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int[] move : moves) {
                Piece[][] tempBoard = cloneBoard(currentBoard);
                executeMove(tempBoard, move[0], move[1], move[2], move[3]);
                int eval = minimax(tempBoard, depth - 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int[] move : moves) {
                Piece[][] tempBoard = cloneBoard(currentBoard);
                executeMove(tempBoard, move[0], move[1], move[2], move[3]);
                int eval = minimax(tempBoard, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private int evaluateBoard(Piece[][] b) {
        int score = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Piece p = b[r][c];
                if (p != null) {
                    int val = 10;
                    if (p.isKing()) val = 50;
                    if (c == 0 || c == 7) val += 2; 
                    if (p.getColor() == Piece.WHITE && r == 0) val += 5;

                    if (p.getColor() == Piece.WHITE) score += val;
                    else score -= val;
                }
            }
        }
        return score;
    }

    private Piece[][] cloneBoard(Piece[][] src) {
        Piece[][] dest = new Piece[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (src[r][c] != null) {
                    Piece p = new Piece(src[r][c].getColor());
                    if (src[r][c].isKing()) p.promote();
                    dest[r][c] = p;
                }
            }
        }
        return dest;
    }

    private List<int[]> getAllLegalMoves(Piece[][] b, int color) {
        List<int[]> moves = new ArrayList<>();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (b[r][c] != null && b[r][c].getColor() == color) {
                    for (int tr = 0; tr < ROWS; tr++) {
                        for (int tc = 0; tc < COLS; tc++) {
                            if (isValidMove(b, r, c, tr, tc)) {
                                moves.add(new int[]{r, c, tr, tc});
                            }
                        }
                    }
                }
            }
        }
        
        // CHECK MANDATORY CAPTURE (if forceCapture is ON)
        List<int[]> captures = new ArrayList<>();
        for (int[] m : moves) {
            if (Math.abs(m[2] - m[0]) >= 2) captures.add(m);
        }
        
        if (forceCapture && !captures.isEmpty()) { 
            return captures;
        }
        
        return moves;
    }

    private boolean isValidMove(Piece[][] b, int r1, int c1, int r2, int c2) {
        // 1. Basic checks
        if (r2 < 0 || r2 >= ROWS || c2 < 0 || c2 >= COLS) return false;
        if (b[r2][c2] != null) return false; // Target must be empty
        if ((r2 + c2) % 2 == 0) return false; // Must be on dark tile

        Piece p = b[r1][c1];
        int dr = r2 - r1;
        int dc = c2 - c1;
        
        if (Math.abs(dr) != Math.abs(dc)) return false; // Must move diagonally

        // 2. King Logic (Flying King / Short King)
        if (p.isKing()) {
            
            if (kingMoveRule == 1) { // Flying King (Long Jump)
                
                int rDir = Integer.signum(dr);
                int cDir = Integer.signum(dc);
                
                int obstacleCount = 0;
                int cr = r1 + rDir;
                int cc = c1 + cDir;
                
                while (cr != r2) {
                    Piece obs = b[cr][cc];
                    if (obs != null) {
                        if (obs.getColor() == p.getColor()) return false; 
                        obstacleCount++;
                    }
                    cr += rDir; 
                    cc += cDir;
                }
                
                if (obstacleCount == 0) return true;
                if (obstacleCount == 1) return true;
                return false;

            } else { // Short King (kingMoveRule == 2)
                // Short King logic: Can move/capture 1 or 2 steps backward/forward
                int absDr = Math.abs(dr);
                if (absDr == 1) { // Move 1 step
                    return true;
                }
                if (absDr == 2) { // Capture 1 piece
                    Piece mid = b[(r1 + r2) / 2][(c1 + c2) / 2];
                    return mid != null && mid.getColor() != p.getColor();
                }
                return false;
            }
        } 
        // 3. Normal Piece Logic
        else {
            // RED (Player) moves FORWARD (up the board) -> dr < 0
            // WHITE (AI) moves FORWARD (down the board) -> dr > 0
            boolean forward = (p.getColor() == Piece.RED) ? (dr < 0) : (dr > 0);
            
            // Move 1 step
            if (Math.abs(dr) == 1) {
                // Must be forward move
                return forward;
            }
            // Capture (Move 2 steps)
            if (Math.abs(dr) == 2) {
                // Must be forward capture
                if (!forward) return false; 

                Piece mid = b[(r1 + r2) / 2][(c1 + c2) / 2];
                return mid != null && mid.getColor() != p.getColor();
            }
        }
        return false;
    }

    private void executeMove(Piece[][] b, int r1, int c1, int r2, int c2) {
        Piece p = b[r1][c1];
        b[r1][c1] = null;
        b[r2][c2] = p;

        // Handle Capturing
        int dr = r2 - r1;
        int dc = c2 - c1;
        
        // If moved more than 1 square, check for captures (works for both Normal and King)
        if (Math.abs(dr) >= 2) {
            int rDir = Integer.signum(dr);
            int cDir = Integer.signum(dc);
            int cr = r1 + rDir;
            int cc = c1 + cDir;
            
            while (cr != r2) {
                if (b[cr][cc] != null) {
                    b[cr][cc] = null; // Remove captured piece
                }
                cr += rDir; 
                cc += cDir;
            }
        }

        // Promotion
        if ((p.getColor() == Piece.RED && r2 == 0) || (p.getColor() == Piece.WHITE && r2 == ROWS - 1)) {
            p.promote();
        }
    }

    /**
     * Check Win Condition
     * Fix: Checks if the player has NO LEGAL MOVES left.
     */
    private void checkWinCondition() {
        // Check Red
        List<int[]> redMoves = getAllLegalMoves(board, Piece.RED);
        if (redMoves.isEmpty()) {
            gameover = true;
            JOptionPane.showMessageDialog(this, "Game Over - AI Wins!");
            returnToMenu();
            return;
        }

        // Check White
        List<int[]> whiteMoves = getAllLegalMoves(board, Piece.WHITE);
        if (whiteMoves.isEmpty()) {
            gameover = true;
            JOptionPane.showMessageDialog(this, "You Win!");
            returnToMenu();
            return;
        }
    }
    
    private void returnToMenu() {
        SwingUtilities.getWindowAncestor(this).dispose();
        new MainMenu().setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Define Theme Colors based on static variable
        Color lightTile, darkTile, redPiece, whitePiece;

        if (boardTheme == 2) { // Dark Mode
            lightTile = new Color(70, 70, 70);
            darkTile = new Color(40, 40, 40);
            redPiece = new Color(255, 100, 100);
            whitePiece = new Color(200, 200, 200);
        } else if (boardTheme == 3) { // Blue Ocean
            lightTile = new Color(173, 216, 230); // Light Blue
            darkTile = new Color(70, 130, 180);  // Steel Blue
            redPiece = new Color(200, 50, 50);
            whitePiece = new Color(255, 255, 255);
        } else if (boardTheme == 4) { // Red Lava
            lightTile = new Color(255, 150, 150); // Light Red/Pink
            darkTile = new Color(180, 0, 0);     // Dark Red
            redPiece = new Color(40, 40, 40);    // Black piece
            whitePiece = new Color(255, 255, 255);
        } else if (boardTheme == 5) { // Marble/Gray
            lightTile = new Color(220, 220, 220); 
            darkTile = new Color(100, 100, 100);
            redPiece = new Color(180, 50, 50);
            whitePiece = new Color(255, 255, 255);
        } else if (boardTheme == 6) { // Neon Pink
            lightTile = new Color(255, 192, 203); 
            darkTile = new Color(255, 0, 127);
            redPiece = new Color(0, 0, 0);
            whitePiece = new Color(255, 255, 255);
        } else { // Default Theme (boardTheme == 1) - Classic Green
            lightTile = new Color(238, 238, 210);
            darkTile = new Color(118, 150, 86);
            redPiece = new Color(200, 50, 50);
            whitePiece = new Color(240, 240, 240);
        }

        // Draw Board
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                g2.setColor((r + c) % 2 == 0 ? lightTile : darkTile);
                g2.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                
                // Highlight Selection
                if (r == selectedRow && c == selectedCol) {
                    g2.setColor(new Color(255, 255, 0, 100));
                    g2.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
                
                // Highlight Valid Moves for Selected Piece (FIXED visibility)
                if (showLegalMoves && selectedRow != -1 && isValidMove(board, selectedRow, selectedCol, r, c)) { 
                    
                    // Use a bright, semi-transparent color that contrasts well
                    g2.setColor(new Color(102, 255, 204, 150)); 
                    g2.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    
                    // Add a dot for better visibility
                    int dotSize = TILE_SIZE / 4;
                    int dotX = c * TILE_SIZE + (TILE_SIZE - dotSize) / 2;
                    int dotY = r * TILE_SIZE + (TILE_SIZE - dotSize) / 2;
                    g2.setColor(new Color(255, 255, 255, 200)); 
                    g2.fillOval(dotX, dotY, dotSize, dotSize);
                }

                // Draw Pieces
                Piece p = board[r][c];
                if (p != null) {
                    int x = c * TILE_SIZE + 10, y = r * TILE_SIZE + 10, s = TILE_SIZE - 20;
                    
                    int style = pieceStyle;
                    
                    // Define specific colors for gradient/outline based on theme choice
                    Color pieceMain = p.getColor() == Piece.RED ? redPiece : whitePiece;
                    Color pieceDark = pieceMain.darker();
                    Color pieceLight = pieceMain.brighter();
                    
                    // Style 1, 2, 5 (Oval/Gradient/Glass)
                    if (style == 1 || style == 2 || style == 5) {
                        
                        GradientPaint gp;
                        if (style == 5) { // Glass/Translucent
                             Color transparentBase = new Color(pieceMain.getRed(), pieceMain.getGreen(), pieceMain.getBlue(), 180);
                             Color transparentHighlight = new Color(pieceDark.getRed(), pieceDark.getGreen(), pieceDark.getBlue(), 120);
                            gp = new GradientPaint(x, y, transparentBase, x + s, y + s, transparentHighlight);
                        } else { // Default Gradient (Style 1, 2)
                            gp = new GradientPaint(x, y, pieceLight, x + s, y + s, pieceDark);
                        }
                        
                        g2.setPaint(gp);
                        g2.fillOval(x, y, s, s);
                        g2.setColor(Color.BLACK);
                        g2.drawOval(x, y, s, s); // Outer border

                        if (style == 2) { // Dot effect
                            g2.setColor(Color.BLACK);
                            g2.drawOval(x + s/4, y + s/4, s/2, s/2);
                        }
                    } 
                    // Style 3 (Square)
                    else if (style == 3) { 
                        g2.setColor(pieceMain);
                        g2.fillRect(x, y, s, s);
                        g2.setColor(Color.BLACK);
                        g2.drawRect(x, y, s, s);
                    } 
                    // Style 4, 6 (Classic/Monochromatic Outline)
                    else if (style == 4 || style == 6) { 
                        Color outlineColor = (style == 6) ? pieceMain.darker().darker().darker() : Color.BLACK;
                        Color mainColor = (style == 6) ? pieceMain.brighter() : pieceMain;
                        
                        // 1. Draw Outline (Thick)
                        g2.setColor(outlineColor); 
                        g2.fillOval(x - 2, y - 2, s + 4, s + 4);
                        // 2. Draw Main Color
                        g2.setColor(mainColor);
                        g2.fillOval(x, y, s, s);
                    }
                    
                    // Draw King Mark
                    if (p.isKing()) {
                        g2.setColor(p.getColor() == Piece.RED ? Color.WHITE : Color.BLACK); 
                        g2.setFont(new Font("Arial", Font.BOLD, 24));
                        FontMetrics fm = g2.getFontMetrics();
                        int tx = x + (s - fm.stringWidth("K")) / 2;
                        int ty = y + (s - fm.getHeight()) / 2 + fm.getAscent();
                        g2.drawString("K", tx, ty);
                    }
                }
            }
        }
        
        // Draw Status Text
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString(gameStatus, 10, 25);
    }
}