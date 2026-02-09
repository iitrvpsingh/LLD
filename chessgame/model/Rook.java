package chessgame.model;

import chessgame.enums.Color;
import chessgame.enums.PieceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Rook piece - moves any distance horizontally or vertically
 */
public class Rook extends Piece {
    
    public Rook(Color color, Position position) {
        super(color, PieceType.ROOK, position);
    }
    
    @Override
    public boolean canMove(Board board, Position to) {
        // Must move horizontally or vertically
        if (position.getRow() != to.getRow() && position.getCol() != to.getCol()) {
            return false;
        }
        
        // Check if path is clear
        if (!board.isPathClear(position, to)) {
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
        
        // 4 directions (horizontal and vertical)
        int[] rowOffsets = {-1, 0, 0, 1};
        int[] colOffsets = {0, -1, 1, 0};
        
        for (int dir = 0; dir < 4; dir++) {
            for (int dist = 1; dist < 8; dist++) {
                int newRow = position.getRow() + rowOffsets[dir] * dist;
                int newCol = position.getCol() + colOffsets[dir] * dist;
                
                if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) {
                    break;
                }
                
                Position newPos = new Position(newRow, newCol);
                if (canMove(board, newPos)) {
                    moves.add(newPos);
                    if (board.getPiece(newPos) != null) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        
        return moves;
    }
}
