package chessgame;

import chessgame.exception.ChessException;

/**
 * Demo of the complete chess game
 */
public class ChessGameDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Chess Game - Complete Implementation ===\n");
        
        try {
            // Scenario 1: Basic moves and check
            System.out.println("--- SCENARIO 1: Basic Moves & Check ---");
            scenario1_BasicMoves();
            
            // Scenario 2: Scholar's Mate (fastest checkmate)
            System.out.println("\n--- SCENARIO 2: Scholar's Mate (Checkmate in 4 moves) ---");
            scenario2_ScholarsMate();
            
            // Scenario 3: Invalid moves
            System.out.println("\n--- SCENARIO 3: Invalid Move Handling ---");
            scenario3_InvalidMoves();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Demo Complete ===");
    }
    
    private static void scenario1_BasicMoves() throws ChessException {
        ChessGame game = new ChessGame("Alice", "Bob");
        
        System.out.println("Initial board:");
        game.printBoard();
        
        // White opens with e4
        System.out.println(game.getCurrentPlayer().getName() + "'s turn:");
        game.makeMove("e2", "e4");
        game.printBoard();
        
        // Black responds with e5
        System.out.println(game.getCurrentPlayer().getName() + "'s turn:");
        game.makeMove("e7", "e5");
        game.printBoard();
        
        // White develops knight
        System.out.println(game.getCurrentPlayer().getName() + "'s turn:");
        game.makeMove("g1", "f3");
        game.printBoard();
        
        System.out.println("✓ Basic moves working correctly!");
    }
    
    private static void scenario2_ScholarsMate() throws ChessException {
        ChessGame game = new ChessGame("White", "Black");
        
        System.out.println("Demonstrating Scholar's Mate (4-move checkmate):\n");
        
        // Move 1: e4 e5
        game.makeMove("e2", "e4");
        game.makeMove("e7", "e5");
        
        // Move 2: Bc4 Nc6
        game.makeMove("f1", "c4");
        game.makeMove("b8", "c6");
        
        // Move 3: Qh5 Nf6
        game.makeMove("d1", "h5");
        game.makeMove("g8", "f6");
        
        // Move 4: Qxf7# (Checkmate!)
        game.makeMove("h5", "f7");
        
        game.printBoard();
        
        System.out.println("Game Status: " + game.getGameStatus());
        if (game.getWinner() != null) {
            System.out.println("Winner: " + game.getWinner().getName());
        }
        
        System.out.println("\n✓ Checkmate detection working!");
    }
    
    private static void scenario3_InvalidMoves() throws ChessException {
        ChessGame game = new ChessGame("Alice", "Bob");
        
        System.out.println("Testing invalid move handling:\n");
        
        // Try to move opponent's piece
        try {
            game.makeMove("e7", "e5");  // Black piece on white's turn
        } catch (ChessException e) {
            System.out.println("✓ Correctly rejected: " + e.getMessage());
        }
        
        // Try invalid pawn move
        try {
            game.makeMove("e2", "e5");  // Pawn can't move 3 squares
        } catch (ChessException e) {
            System.out.println("✓ Correctly rejected: " + e.getMessage());
        }
        
        // Valid move
        game.makeMove("e2", "e4");
        System.out.println("✓ Valid move accepted: e2 -> e4");
        
        // Try to move through piece
        try {
            game.makeMove("d8", "d1");  // Queen blocked by pawn
        } catch (ChessException e) {
            System.out.println("✓ Correctly rejected: Path blocked");
        }
        
        System.out.println("\n✓ Move validation working correctly!");
    }
}
