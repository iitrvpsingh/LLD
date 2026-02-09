package chessgame.enums;

/**
 * Color of chess pieces
 */
public enum Color {
    WHITE,
    BLACK;
    
    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
