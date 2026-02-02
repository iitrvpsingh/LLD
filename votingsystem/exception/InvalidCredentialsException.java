package votingsystem.exception;

public class InvalidCredentialsException extends VotingException {
    public InvalidCredentialsException() {
        super("Invalid voter credentials");
    }
}
