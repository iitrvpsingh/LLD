package chessgame;

import chessgame.enums.Color;
import chessgame.enums.GameStatus;
import chessgame.exception.ChessException;
import chessgame.exception.InvalidMoveException;
import chessgame.model.*;

import java.util.List;

/**
 * Main Chess Game class
 * 
 * Complete implementation with:
 * - Full move validation
 * - Check/checkmate/stalemate detection
 * - Move history
 * - Turn management
 * 
 * Design Patterns:
 * - Facade Pattern: Simplified interface to complex chess logic
 * - Strategy Pattern: Each piece has its own movement strategy
 */
public class ChessGame {
    private final Board board;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private Player currentPlayer;
    private GameStatus gameStatus;
    
    public ChessGame(String whiteName, String blackName) {
        this.board = new Board();
        this.whitePlayer = new Player(whiteName, Color.WHITE);
        this.blackPlayer = new Player(blackName, Color.BLACK);
        this.currentPlayer = whitePlayer;
        this.gameStatus = GameStatus.ACTIVE;
    }
    
    /**
     * Make a move
     */
    public void makeMove(String from, String to) throws ChessException {
        if (gameStatus != GameStatus.ACTIVE && gameStatus != GameStatus.CHECK) {
            throw new ChessException("Game is over");
        }
        
        Position fromPos = Position.fromChessNotation(from);
        Position toPos = Position.fromChessNotation(to);
        
        Piece piece = board.getPiece(fromPos);
        if (piece == null) {
            throw new InvalidMoveException("No piece at " + from);
        }
        
        if (piece.getColor() != currentPlayer.getColor()) {
            throw new InvalidMoveException("Not your piece! It's " + currentPlayer.getName() + "'s turn");
        }
        
        // Make the move
        board.makeMove(new Move(fromPos, toPos));
        
        System.out.println(currentPlayer.getName() + " moved: " + from + " -> " + to);
        
        // Update game status
        updateGameStatus();
        
        // Switch player
        switchPlayer();
    }
    
    /**
     * Make a move using Position objects
     */
    public void makeMove(Position from, Position to) throws ChessException {
        makeMove(from.toChessNotation(), to.toChessNotation());
    }
    
    /**
     * Update game status after a move
     */
    private void updateGameStatus() {
        Color opponentColor = currentPlayer.getColor().opposite();
        
        if (board.isCheckmate(opponentColor)) {
            gameStatus = GameStatus.CHECKMATE;
            System.out.println("🎉 CHECKMATE! " + currentPlayer.getName() + " wins!");
        } else if (board.isStalemate(opponentColor)) {
            gameStatus = GameStatus.STALEMATE;
            System.out.println("🤝 STALEMATE! Game is a draw!");
        } else if (board.isInCheck(opponentColor)) {
            gameStatus = GameStatus.CHECK;
            System.out.println("⚠️ CHECK!");
        } else {
            gameStatus = GameStatus.ACTIVE;
        }
    }
    
    /**
     * Switch to next player
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }
    
    /**
     * Get current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Get game status
     */
    public GameStatus getGameStatus() {
        return gameStatus;
    }
    
    /**
     * Check if game is over
     */
    public boolean isGameOver() {
        return gameStatus == GameStatus.CHECKMATE || 
               gameStatus == GameStatus.STALEMATE ||
               gameStatus == GameStatus.RESIGNATION ||
               gameStatus == GameStatus.DRAW;
    }
    
    /**
     * Get winner (null if draw or game not over)
     */
    public Player getWinner() {
        if (gameStatus == GameStatus.CHECKMATE) {
            // Current player made the winning move
            return currentPlayer == whitePlayer ? blackPlayer : whitePlayer;
        }
        return null;
    }
    
    /**
     * Resign
     */
    public void resign() {
        gameStatus = GameStatus.RESIGNATION;
        Player winner = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
        System.out.println(currentPlayer.getName() + " resigned. " + winner.getName() + " wins!");
    }
    
    /**
     * Print the board
     */
    public void printBoard() {
        board.printBoard();
    }
    
    /**
     * Get move history
     */
    public List<Move> getMoveHistory() {
        return board.getMoveHistory();
    }
    
    /**
     * Get board
     */
    public Board getBoard() {
        return board;
    }
}
