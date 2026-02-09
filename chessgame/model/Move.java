package chessgame.model;

import chessgame.enums.PieceType;

/**
 * Represents a chess move from one position to another
 */
public class Move {
    private final Position from;
    private final Position to;
    private final Piece capturedPiece;
    private final boolean isCastling;
    private final boolean isEnPassant;
    private final PieceType promotionType;
    
    public Move(Position from, Position to) {
        this(from, to, null, false, false, null);
    }
    
    public Move(Position from, Position to, Piece capturedPiece) {
        this(from, to, capturedPiece, false, false, null);
    }
    
    public Move(Position from, Position to, Piece capturedPiece, 
                boolean isCastling, boolean isEnPassant, PieceType promotionType) {
        this.from = from;
        this.to = to;
        this.capturedPiece = capturedPiece;
        this.isCastling = isCastling;
        this.isEnPassant = isEnPassant;
        this.promotionType = promotionType;
    }
    
    public Position getFrom() {
        return from;
    }
    
    public Position getTo() {
        return to;
    }
    
    public Piece getCapturedPiece() {
        return capturedPiece;
    }
    
    public boolean isCastling() {
        return isCastling;
    }
    
    public boolean isEnPassant() {
        return isEnPassant;
    }
    
    public PieceType getPromotionType() {
        return promotionType;
    }
    
    @Override
    public String toString() {
        return from + " -> " + to;
    }
}
