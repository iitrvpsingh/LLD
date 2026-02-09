package chessgame.model;

import chessgame.enums.Color;
import chessgame.enums.PieceType;

import java.util.ArrayList;
import java.util.List;

/**
 * King piece - moves one square in any direction
 */
public class King extends Piece {
    
    public King(Color color, Position position) {
        super(color, PieceType.KING, position);
    }
    
    @Override
    public boolean canMove(Board board, Position to) {
        int rowDiff = Math.abs(to.getRow() - position.getRow());
        int colDiff = Math.abs(to.getCol() - position.getCol());
        
        // King moves one square in any direction
        if (rowDiff > 1 || colDiff > 1) {
            return false;
        }
        
        // Can't capture own piece
        Piece targetPiece = board.getPiece(to);
        if (targetPiece != null && targetPiece.getColor() == this.color) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        
        // All 8 directions
        int[] rowOffsets = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colOffsets = {-1, 0, 1, -1, 1, -1, 0, 1};
        
        for (int i = 0; i < 8; i++) {
            int newRow = position.getRow() + rowOffsets[i];
            int newCol = position.getCol() + colOffsets[i];
            
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Position newPos = new Position(newRow, newCol);
                if (canMove(board, newPos)) {
                    moves.add(newPos);
                }
            }
        }
        
        return moves;
    }
}
