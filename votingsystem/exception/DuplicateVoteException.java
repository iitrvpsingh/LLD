package votingsystem.exception;

public class DuplicateVoteException extends VotingException {
    public DuplicateVoteException(String voterId) {
        super("Voter " + voterId + " has already cast their vote");
    }
}
