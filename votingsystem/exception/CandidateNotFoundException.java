package votingsystem.exception;

public class CandidateNotFoundException extends VotingException {
    public CandidateNotFoundException(String candidateId) {
        super("Candidate not found with ID: " + candidateId);
    }
}
