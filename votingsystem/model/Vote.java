package votingsystem.model;

import votingsystem.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a vote cast in an election
 * Note: Does NOT store voterId to maintain ballot secrecy
 */
public class Vote {
    private final String voteId;
    private final String electionId;
    private final String candidateId;
    private final LocalDateTime timestamp;

    public Vote(String electionId, String candidateId) {
        ValidationUtil.validateNotEmpty(electionId, "Election ID");
        ValidationUtil.validateNotEmpty(candidateId, "Candidate ID");
        
        this.voteId = UUID.randomUUID().toString();
        this.electionId = electionId;
        this.candidateId = candidateId;
        this.timestamp = LocalDateTime.now();
    }

    public String getVoteId() {
        return voteId;
    }

    public String getElectionId() {
        return electionId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(voteId, vote.voteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voteId);
    }

    @Override
    public String toString() {
        return "Vote{" +
                "voteId='" + voteId + '\'' +
                ", electionId='" + electionId + '\'' +
                ", candidateId='" + candidateId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
