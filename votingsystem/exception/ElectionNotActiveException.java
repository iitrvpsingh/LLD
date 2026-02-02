package votingsystem.exception;

public class ElectionNotActiveException extends VotingException {
    public ElectionNotActiveException(String electionId) {
        super("Election " + electionId + " is not currently active");
    }
}
