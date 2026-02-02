package votingsystem_distributed.repository;

import votingsystem_distributed.model.Candidate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for Candidate persistence
 */
public class CandidateRepository {
    private final Map<String, Candidate> database = new ConcurrentHashMap<>();

    public void save(Candidate candidate) {
        database.put(candidate.getId(), candidate);
    }

    public Optional<Candidate> findById(String id) {
        return Optional.ofNullable(database.get(id));
    }

    public List<Candidate> findAll() {
        return new ArrayList<>(database.values());
    }

    public List<Candidate> findAllById(Collection<String> ids) {
        return ids.stream()
                .map(database::get)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean existsById(String id) {
        return database.containsKey(id);
    }

    public void deleteById(String id) {
        database.remove(id);
    }
}
