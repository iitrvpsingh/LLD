package votingsystem.exception;

public class VoterIneligibleException extends VotingException {
    public VoterIneligibleException(String voterId) {
        super("Voter " + voterId + " is not eligible to vote");
    }
}
