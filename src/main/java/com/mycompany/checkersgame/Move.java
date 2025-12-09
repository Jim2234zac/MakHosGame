public class Move {
    private int startRow, startCol;
    private int endRow, endCol;

    public Move(int r1, int c1, int r2, int c2) {
        this.startRow = r1;
        this.startCol = c1;
        this.endRow = r2;
        this.endCol = c2;
    }

    public int getStartRow() { return startRow; }
    public int getStartCol() { return startCol; }
    public int getEndRow() { return endRow; }
    public int getEndCol() { return endCol; }
}