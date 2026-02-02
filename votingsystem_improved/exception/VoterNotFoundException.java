package votingsystem_improved.exception;

public class VoterNotFoundException extends VotingException {
    public VoterNotFoundException(String voterId) {
        super("Voter not found with ID: " + voterId);
    }
}
