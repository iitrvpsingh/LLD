package chessgame.model;

import chessgame.enums.Color;
import chessgame.enums.PieceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Knight piece - moves in L-shape (2 squares in one direction, 1 in perpendicular)
 */
public class Knight extends Piece {
    
    public Knight(Color color, Position position) {
        super(color, PieceType.KNIGHT, position);
    }
    
    @Override
    public boolean canMove(Board board, Position to) {
        int rowDiff = Math.abs(to.getRow() - position.getRow());
        int colDiff = Math.abs(to.getCol() - position.getCol());
        
        // Must move in L-shape: 2+1 or 1+2
        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) {
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
        
        // All 8 possible L-shaped moves
        int[] rowOffsets = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] colOffsets = {-1, 1, -2, 2, -2, 2, -1, 1};
        
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
