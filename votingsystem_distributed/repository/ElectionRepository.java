package votingsystem_distributed.repository;

import votingsystem_distributed.model.Election;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repository for Election persistence
 */
public class ElectionRepository {
    private final Map<String, Election> database = new ConcurrentHashMap<>();

    public void save(Election election) {
        database.put(election.getId(), election);
    }

    public Optional<Election> findById(String id) {
        return Optional.ofNullable(database.get(id));
    }

    public List<Election> findAll() {
        return new ArrayList<>(database.values());
    }

    public List<Election> findByStatus(String status) {
        return database.values().stream()
                .filter(e -> e.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public boolean existsById(String id) {
        return database.containsKey(id);
    }

    public void deleteById(String id) {
        database.remove(id);
    }
}
