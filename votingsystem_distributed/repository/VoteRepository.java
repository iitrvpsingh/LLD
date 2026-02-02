package votingsystem_distributed.repository;

import votingsystem_distributed.model.Vote;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repository for Vote persistence
 * Stores WHAT was voted (without voter information)
 */
public class VoteRepository {
    private final Map<String, Vote> database = new ConcurrentHashMap<>();

    public void save(Vote vote) {
        database.put(vote.getVoteId(), vote);
    }

    public Optional<Vote> findById(String voteId) {
        return Optional.ofNullable(database.get(voteId));
    }

    public List<Vote> findByElectionId(String electionId) {
        return database.values().stream()
                .filter(v -> v.getElectionId().equals(electionId))
                .collect(Collectors.toList());
    }

    public long countByElectionId(String electionId) {
        return database.values().stream()
                .filter(v -> v.getElectionId().equals(electionId))
                .count();
    }

    public Map<String, Long> countByCandidateForElection(String electionId) {
        return database.values().stream()
                .filter(v -> v.getElectionId().equals(electionId))
                .collect(Collectors.groupingBy(Vote::getCandidateId, Collectors.counting()));
    }

    public List<Vote> findAll() {
        return new ArrayList<>(database.values());
    }
}
