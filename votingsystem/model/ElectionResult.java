package votingsystem.model;

import java.util.*;

/**
 * Represents the results of an election
 * Immutable value object
 */
public class ElectionResult {
    private final String electionId;
    private final Map<String, Integer> candidateVoteCounts;
    private final int totalVotes;
    private final String winnerId;

    public ElectionResult(String electionId, Map<String, Integer> candidateVoteCounts) {
        this.electionId = electionId;
        this.candidateVoteCounts = new HashMap<>(candidateVoteCounts);
        this.totalVotes = candidateVoteCounts.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        this.winnerId = determineWinner(candidateVoteCounts);
    }

    private String determineWinner(Map<String, Integer> voteCounts) {
        return voteCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public String getElectionId() {
        return electionId;
    }

    public Map<String, Integer> getCandidateVoteCounts() {
        return Collections.unmodifiableMap(candidateVoteCounts);
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public int getVotesForCandidate(String candidateId) {
        return candidateVoteCounts.getOrDefault(candidateId, 0);
    }

    @Override
    public String toString() {
        return "ElectionResult{" +
                "electionId='" + electionId + '\'' +
                ", totalVotes=" + totalVotes +
                ", winnerId='" + winnerId + '\'' +
                ", voteCounts=" + candidateVoteCounts +
                '}';
    }
}
