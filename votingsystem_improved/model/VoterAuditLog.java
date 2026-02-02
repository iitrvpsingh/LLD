package votingsystem_improved.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Audit log for tracking WHO voted (without revealing their choice)
 * Maintains ballot secrecy by not storing candidate information
 */
public class VoterAuditLog {
    private final String voterId;
    private final String electionId;
    private final LocalDateTime timestamp;

    public VoterAuditLog(String voterId, String electionId) {
        this.voterId = voterId;
        this.electionId = electionId;
        this.timestamp = LocalDateTime.now();
    }

    public String getVoterId() {
        return voterId;
    }

    public String getElectionId() {
        return electionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoterAuditLog that = (VoterAuditLog) o;
        return Objects.equals(voterId, that.voterId) && 
               Objects.equals(electionId, that.electionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voterId, electionId);
    }

    @Override
    public String toString() {
        return "VoterAuditLog{" +
                "voterId='" + voterId + '\'' +
                ", electionId='" + electionId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
