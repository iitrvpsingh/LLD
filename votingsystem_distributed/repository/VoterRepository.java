package votingsystem_distributed.repository;

import votingsystem_distributed.model.Voter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for Voter persistence
 * In production: Replace with JPA/Hibernate repository
 * 
 * @JpaRepository example:
 * public interface VoterRepository extends JpaRepository<Voter, String> {
 *     Optional<Voter> findByEmail(String email);
 * }
 */
public class VoterRepository {
    // Simulates database - in production, use actual database
    private final Map<String, Voter> database = new ConcurrentHashMap<>();

    public void save(Voter voter) {
        database.put(voter.getId(), voter);
    }

    public Optional<Voter> findById(String id) {
        return Optional.ofNullable(database.get(id));
    }

    public Optional<Voter> findByEmail(String email) {
        return database.values().stream()
                .filter(v -> v.getEmail().equals(email))
                .findFirst();
    }

    public List<Voter> findAll() {
        return new ArrayList<>(database.values());
    }

    public boolean existsById(String id) {
        return database.containsKey(id);
    }

    public void deleteById(String id) {
        database.remove(id);
    }

    public long count() {
        return database.size();
    }
}
