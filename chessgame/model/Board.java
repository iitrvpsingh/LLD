package chessgame.model;

import chessgame.enums.Color;
import chessgame.enums.PieceType;
import chessgame.exception.InvalidMoveException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chess board with complete game logic
 */
public class Board {
    private final Map<Position, Piece> pieces;
    private final List<Move> moveHistory;
    
    public Board() {
        this.pieces = new HashMap<>();
        this.moveHistory = new ArrayList<>();
        setupInitialPosition();
    }
    
    /**
     * Setup initial chess position
     */
    private void setupInitialPosition() {
        // White pieces (rows 6-7)
        pieces.put(new Position(7, 0), new Rook(Color.WHITE, new Position(7, 0)));
        pieces.put(new Position(7, 1), new Knight(Color.WHITE, new Position(7, 1)));
        pieces.put(new Position(7, 2), new Bishop(Color.WHITE, new Position(7, 2)));
        pieces.put(new Position(7, 3), new Queen(Color.WHITE, new Position(7, 3)));
        pieces.put(new Position(7, 4), new King(Color.WHITE, new Position(7, 4)));
        pieces.put(new Position(7, 5), new Bishop(Color.WHITE, new Position(7, 5)));
        pieces.put(new Position(7, 6), new Knight(Color.WHITE, new Position(7, 6)));
        pieces.put(new Position(7, 7), new Rook(Color.WHITE, new Position(7, 7)));
        
        for (int col = 0; col < 8; col++) {
            pieces.put(new Position(6, col), new Pawn(Color.WHITE, new Position(6, col)));
        }
        
        // Black pieces (rows 0-1)
        pieces.put(new Position(0, 0), new Rook(Color.BLACK, new Position(0, 0)));
        pieces.put(new Position(0, 1), new Knight(Color.BLACK, new Position(0, 1)));
        pieces.put(new Position(0, 2), new Bishop(Color.BLACK, new Position(0, 2)));
        pieces.put(new Position(0, 3), new Queen(Color.BLACK, new Position(0, 3)));
        pieces.put(new Position(0, 4), new King(Color.BLACK, new Position(0, 4)));
        pieces.put(new Position(0, 5), new Bishop(Color.BLACK, new Position(0, 5)));
        pieces.put(new Position(0, 6), new Knight(Color.BLACK, new Position(0, 6)));
        pieces.put(new Position(0, 7), new Rook(Color.BLACK, new Position(0, 7)));
        
        for (int col = 0; col < 8; col++) {
            pieces.put(new Position(1, col), new Pawn(Color.BLACK, new Position(1, col)));
        }
    }
    
    public Piece getPiece(Position position) {
        return pieces.get(position);
    }
    
    /**
     * Make a move on the board
     */
    public void makeMove(Move move) throws InvalidMoveException {
        Position from = move.getFrom();
        Position to = move.getTo();
        
        Piece piece = getPiece(from);
        if (piece == null) {
            throw new InvalidMoveException("No piece at " + from);
        }
        
        // Validate move
        if (!piece.canMove(this, to)) {
            throw new InvalidMoveException("Invalid move for " + piece.getType());
        }
        
        // Check if move would put own king in check
        if (wouldBeInCheck(piece.getColor(), move)) {
            throw new InvalidMoveException("Move would put king in check");
        }
        
        // Execute move
        Piece capturedPiece = getPiece(to);
        pieces.remove(from);
        pieces.put(to, piece);
        piece.setPosition(to);
        
        // Record move
        moveHistory.add(new Move(from, to, capturedPiece));
    }
    
    /**
     * Check if path between two positions is clear
     */
    public boolean isPathClear(Position from, Position to) {
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getCol() - from.getCol();
        
        int rowDir = Integer.signum(rowDiff);
        int colDir = Integer.signum(colDiff);
        
        int steps = Math.max(Math.abs(rowDiff), Math.abs(colDiff));
        
        for (int i = 1; i < steps; i++) {
            Position intermediate = new Position(
                from.getRow() + i * rowDir,
                from.getCol() + i * colDir
            );
            if (getPiece(intermediate) != null) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if a color's king is in check
     */
    public boolean isInCheck(Color color) {
        Position kingPos = findKing(color);
        if (kingPos == null) return false;
        
        return isSquareUnderAttack(kingPos, color.opposite());
    }
    
    /**
     * Check if a square is under attack by a color
     */
    public boolean isSquareUnderAttack(Position position, Color attackingColor) {
        for (Piece piece : getAllPieces(attackingColor)) {
            if (piece.canMove(this, position)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Find king position for a color
     */
    public Position findKing(Color color) {
        for (Map.Entry<Position, Piece> entry : pieces.entrySet()) {
            Piece piece = entry.getValue();
            if (piece.getType() == PieceType.KING && piece.getColor() == color) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * Get all pieces of a color
     */
    public List<Piece> getAllPieces(Color color) {
        List<Piece> result = new ArrayList<>();
        for (Piece piece : pieces.values()) {
            if (piece.getColor() == color) {
                result.add(piece);
            }
        }
        return result;
    }
    
    /**
     * Check if move would put own king in check
     */
    private boolean wouldBeInCheck(Color color, Move move) {
        // Simulate the move
        Piece piece = getPiece(move.getFrom());
        Piece captured = getPiece(move.getTo());
        
        pieces.remove(move.getFrom());
        pieces.put(move.getTo(), piece);
        Position oldPos = piece.getPosition();
        piece.setPosition(move.getTo());
        
        boolean inCheck = isInCheck(color);
        
        // Undo the move
        pieces.put(move.getFrom(), piece);
        if (captured != null) {
            pieces.put(move.getTo(), captured);
        } else {
            pieces.remove(move.getTo());
        }
        piece.setPosition(oldPos);
        
        return inCheck;
    }
    
    /**
     * Check if it's checkmate
     */
    public boolean isCheckmate(Color color) {
        if (!isInCheck(color)) {
            return false;
        }
        
        return !hasLegalMoves(color);
    }
    
    /**
     * Check if it's stalemate
     */
    public boolean isStalemate(Color color) {
        if (isInCheck(color)) {
            return false;
        }
        
        return !hasLegalMoves(color);
    }
    
    /**
     * Check if a color has any legal moves
     */
    private boolean hasLegalMoves(Color color) {
        for (Piece piece : getAllPieces(color)) {
            for (Position move : piece.getPossibleMoves(this)) {
                Move testMove = new Move(piece.getPosition(), move);
                if (!wouldBeInCheck(color, testMove)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Print the board
     */
    public void printBoard() {
        System.out.println("\n  a b c d e f g h");
        System.out.println("  ---------------");
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + "|");
            for (int col = 0; col < 8; col++) {
                Piece piece = getPiece(new Position(row, col));
                if (piece == null) {
                    System.out.print(" .");
                } else {
                    String symbol = piece.getType().getSymbol();
                    if (piece.getColor() == Color.BLACK) {
                        symbol = symbol.toLowerCase();
                    }
                    System.out.print(" " + symbol);
                }
            }
            System.out.println("|" + (8 - row));
        }
        System.out.println("  ---------------");
        System.out.println("  a b c d e f g h\n");
    }
    
    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }
}
