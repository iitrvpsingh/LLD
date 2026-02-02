package votingsystem_distributed.dto;

/**
 * Data Transfer Object for vote requests
 * Used in REST API
 */
public class VoteRequest {
    private String electionId;
    private String candidateId;
    private String idempotencyKey; // For preventing duplicate votes on network retry

    public VoteRequest() {}

    public VoteRequest(String electionId, String candidateId, String idempotencyKey) {
        this.electionId = electionId;
        this.candidateId = candidateId;
        this.idempotencyKey = idempotencyKey;
    }

    public String getElectionId() { return electionId; }
    public void setElectionId(String electionId) { this.electionId = electionId; }

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
