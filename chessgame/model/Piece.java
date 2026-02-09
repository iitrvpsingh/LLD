package chessgame.model;

import chessgame.enums.Color;
import chessgame.enums.PieceType;

/**
 * Base class for all chess pieces
 */
public abstract class Piece {
    protected final Color color;
    protected final PieceType type;
    protected Position position;
    protected boolean hasMoved;
    
    public Piece(Color color, PieceType type, Position position) {
        this.color = color;
        this.type = type;
        this.position = position;
        this.hasMoved = false;
    }
    
    /**
     * Check if this piece can move to the target position
     * This checks piece-specific movement rules only
     */
    public abstract boolean canMove(Board board, Position to);
    
    /**
     * Get all possible moves for this piece (without checking for check)
     */
    public abstract java.util.List<Position> getPossibleMoves(Board board);
    
    public Color getColor() {
        return color;
    }
    
    public PieceType getType() {
        return type;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
        this.hasMoved = true;
    }
    
    public boolean hasMoved() {
        return hasMoved;
    }
    
    @Override
    public String toString() {
        return color + " " + type + " at " + position;
    }
}
