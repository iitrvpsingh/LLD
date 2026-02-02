package votingsystem_improved.service;

import votingsystem_improved.model.Vote;
import votingsystem_improved.model.VoterAuditLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for maintaining audit logs
 * Maintains ballot secrecy by keeping voter logs and vote logs separate
 * Follows Single Responsibility Principle - only handles audit logging
 */
public class AuditService {
    // Tracks WHO voted (without revealing their choice)
    private final Map<String, List<VoterAuditLog>> voterLogs;
    
    // Tracks WHAT votes were cast (without revealing who cast them)
    private final Map<String, List<Vote>> voteLogs;

    public AuditService() {
        this.voterLogs = new ConcurrentHashMap<>();
        this.voteLogs = new ConcurrentHashMap<>();
    }

    /**
     * Log that a voter participated in an election
     * Does NOT record their vote choice
     */
    public void logVoterParticipation(String voterId, String electionId) {
        VoterAuditLog log = new VoterAuditLog(voterId, electionId);
        voterLogs.computeIfAbsent(electionId, k -> new ArrayList<>()).add(log);
    }

    /**
     * Log a vote cast in an election
     * Does NOT record who cast it
     */
    public void logVote(Vote vote) {
        voteLogs.computeIfAbsent(vote.getElectionId(), k -> new ArrayList<>()).add(vote);
    }

    /**
     * Check if a voter has already voted in an election
     */
    public boolean hasVoterVoted(String voterId, String electionId) {
        List<VoterAuditLog> logs = voterLogs.get(electionId);
        if (logs == null) {
            return false;
        }
        return logs.stream().anyMatch(log -> log.getVoterId().equals(voterId));
    }

    /**
     * Get all voter participation logs for an election
     * Returns WHO voted, but not their choices
     */
    public List<VoterAuditLog> getVoterLogsForElection(String electionId) {
        return new ArrayList<>(voterLogs.getOrDefault(electionId, new ArrayList<>()));
    }

    /**
     * Get all votes for an election
     * Returns WHAT was voted, but not by whom
     */
    public List<Vote> getVotesForElection(String electionId) {
        return new ArrayList<>(voteLogs.getOrDefault(electionId, new ArrayList<>()));
    }

    /**
     * Get total number of voters who participated in an election
     */
    public int getVoterTurnout(String electionId) {
        List<VoterAuditLog> logs = voterLogs.get(electionId);
        return logs != null ? logs.size() : 0;
    }

    /**
     * Get total number of votes cast in an election
     */
    public int getTotalVotes(String electionId) {
        List<Vote> votes = voteLogs.get(electionId);
        return votes != null ? votes.size() : 0;
    }

    /**
     * Get vote count by candidate for an election
     */
    public Map<String, Long> getVoteCountByCandidate(String electionId) {
        List<Vote> votes = voteLogs.getOrDefault(electionId, new ArrayList<>());
        return votes.stream()
                .collect(Collectors.groupingBy(Vote::getCandidateId, Collectors.counting()));
    }
}
