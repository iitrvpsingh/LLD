package chessgame.model;

import chessgame.enums.Color;
import chessgame.enums.PieceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Pawn piece - moves forward, captures diagonally
 */
public class Pawn extends Piece {
    
    public Pawn(Color color, Position position) {
        super(color, PieceType.PAWN, position);
    }
    
    @Override
    public boolean canMove(Board board, Position to) {
        int direction = (color == Color.WHITE) ? -1 : 1;  // White moves up, Black moves down
        int startRow = (color == Color.WHITE) ? 6 : 1;
        
        int rowDiff = to.getRow() - position.getRow();
        int colDiff = Math.abs(to.getCol() - position.getCol());
        
        // Move forward one square
        if (rowDiff == direction && colDiff == 0) {
            return board.getPiece(to) == null;
        }
        
        // Move forward two squares from starting position
        if (rowDiff == 2 * direction && colDiff == 0 && position.getRow() == startRow) {
            Position intermediate = new Position(position.getRow() + direction, position.getCol());
            return board.getPiece(intermediate) == null && board.getPiece(to) == null;
        }
        
        // Capture diagonally
        if (rowDiff == direction && colDiff == 1) {
            Piece targetPiece = board.getPiece(to);
            return targetPiece != null && targetPiece.getColor() != this.color;
        }
        
        return false;
    }
    
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int direction = (color == Color.WHITE) ? -1 : 1;
        int startRow = (color == Color.WHITE) ? 6 : 1;
        
        // Forward one square
        int newRow = position.getRow() + direction;
        if (newRow >= 0 && newRow < 8) {
            Position forward = new Position(newRow, position.getCol());
            if (board.getPiece(forward) == null) {
                moves.add(forward);
                
                // Forward two squares from start
                if (position.getRow() == startRow) {
                    Position forwardTwo = new Position(position.getRow() + 2 * direction, position.getCol());
                    if (board.getPiece(forwardTwo) == null) {
                        moves.add(forwardTwo);
                    }
                }
            }
        }
        
        // Diagonal captures
        for (int colOffset : new int[]{-1, 1}) {
            int newCol = position.getCol() + colOffset;
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Position diagonal = new Position(newRow, newCol);
                Piece targetPiece = board.getPiece(diagonal);
                if (targetPiece != null && targetPiece.getColor() != this.color) {
                    moves.add(diagonal);
                }
            }
        }
        
        return moves;
    }
}
