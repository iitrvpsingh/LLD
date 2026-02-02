package votingsystem_improved.exception;

public class CandidateNotFoundException extends VotingException {
    public CandidateNotFoundException(String candidateId) {
        super("Candidate not found with ID: " + candidateId);
    }
}
