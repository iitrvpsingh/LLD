package votingsystem_distributed.repository;

import votingsystem_distributed.model.VoterAuditLog;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repository for VoterAuditLog persistence
 * Stores WHO voted (without vote choice information)
 * 
 * IMPORTANT: Has unique constraint on (voterId, electionId) to prevent duplicate voting
 */
public class VoterAuditLogRepository {
    private final Map<String, VoterAuditLog> database = new ConcurrentHashMap<>();

    /**
     * Save voter audit log
     * Throws exception if voter already voted in this election (simulates DB unique constraint)
     */
    public void save(VoterAuditLog log) {
        String key = log.getVoterId() + ":" + log.getElectionId();
        
        // Simulate database unique constraint
        if (database.containsKey(key)) {
            throw new IllegalStateException("Duplicate vote: Voter " + log.getVoterId() + 
                    " already voted in election " + log.getElectionId());
        }
        
        database.put(key, log);
    }

    public boolean existsByVoterIdAndElectionId(String voterId, String electionId) {
        String key = voterId + ":" + electionId;
        return database.containsKey(key);
    }

    public List<VoterAuditLog> findByElectionId(String electionId) {
        return database.values().stream()
                .filter(log -> log.getElectionId().equals(electionId))
                .collect(Collectors.toList());
    }

    public long countByElectionId(String electionId) {
        return database.values().stream()
                .filter(log -> log.getElectionId().equals(electionId))
                .count();
    }

    public List<VoterAuditLog> findAll() {
        return new ArrayList<>(database.values());
    }
}
