package chessgame.model;

import java.util.Objects;

/**
 * Represents a position on the chess board
 * Immutable value object
 */
public class Position {
    private final int row;
    private final int col;
    
    public Position(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException("Invalid position: (" + row + ", " + col + ")");
        }
        this.row = row;
        this.col = col;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
    
    /**
     * Get chess notation (e.g., "e4")
     */
    public String toChessNotation() {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }
    
    /**
     * Create position from chess notation (e.g., "e4")
     */
    public static Position fromChessNotation(String notation) {
        if (notation == null || notation.length() != 2) {
            throw new IllegalArgumentException("Invalid notation: " + notation);
        }
        char file = notation.charAt(0);
        char rank = notation.charAt(1);
        
        int col = file - 'a';
        int row = 8 - (rank - '0');
        
        return new Position(row, col);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
    
    @Override
    public String toString() {
        return toChessNotation();
    }
}
