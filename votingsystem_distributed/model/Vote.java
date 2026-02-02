package votingsystem_distributed.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Vote entity - does NOT contain voterId for ballot secrecy
 */
public class Vote {
    private String voteId;
    private String electionId;
    private String candidateId;
    private LocalDateTime timestamp;

    public Vote() {
        this.voteId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public Vote(String electionId, String candidateId) {
        this.voteId = UUID.randomUUID().toString();
        this.electionId = electionId;
        this.candidateId = candidateId;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getVoteId() { return voteId; }
    public void setVoteId(String voteId) { this.voteId = voteId; }

    public String getElectionId() { return electionId; }
    public void setElectionId(String electionId) { this.electionId = electionId; }

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Vote{voteId='" + voteId + "', electionId='" + electionId + 
               "', candidateId='" + candidateId + "', timestamp=" + timestamp + "}";
    }
}
