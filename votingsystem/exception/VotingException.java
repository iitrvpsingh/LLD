package votingsystem.exception;

/**
 * Base exception for all voting system related errors
 */
public class VotingException extends Exception {
    public VotingException(String message) {
        super(message);
    }

    public VotingException(String message, Throwable cause) {
        super(message, cause);
    }
}
