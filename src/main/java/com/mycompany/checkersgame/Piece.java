/**
 * Piece.java
 * Represents a checker piece.
 */
public class Piece {
    
    // Constants for colors
    public static final int RED = 1;   // Player
    public static final int WHITE = 2; // AI

    private int color;
    private boolean isKing;

    public Piece(int color) {
        this.color = color;
        this.isKing = false;
    }

    public int getColor() {
        return color;
    }

    public boolean isKing() {
        return isKing;
    }

    public void promote() {
        this.isKing = true;
    }
}