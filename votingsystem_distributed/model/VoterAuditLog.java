package votingsystem_distributed.model;

import java.time.LocalDateTime;

/**
 * Audit log for tracking WHO voted (without revealing their choice)
 * Maintains ballot secrecy
 */
public class VoterAuditLog {
    private String voterId;
    private String electionId;
    private LocalDateTime timestamp;

    public VoterAuditLog() {
        this.timestamp = LocalDateTime.now();
    }

    public VoterAuditLog(String voterId, String electionId) {
        this.voterId = voterId;
        this.electionId = electionId;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getVoterId() { return voterId; }
    public void setVoterId(String voterId) { this.voterId = voterId; }

    public String getElectionId() { return electionId; }
    public void setElectionId(String electionId) { this.electionId = electionId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "VoterAuditLog{voterId='" + voterId + "', electionId='" + electionId + 
               "', timestamp=" + timestamp + "}";
    }
}
