package votingsystem_improved.service;

import votingsystem_improved.enums.ElectionStatus;
import votingsystem_improved.exception.CandidateNotFoundException;
import votingsystem_improved.model.Candidate;
import votingsystem_improved.model.Election;
import votingsystem_improved.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing elections and candidates
 * Follows Single Responsibility Principle - manages election lifecycle
 */
public class ElectionService {
    private final Map<String, Election> elections;
    private final Map<String, Candidate> candidates;

    public ElectionService() {
        this.elections = new ConcurrentHashMap<>();
        this.candidates = new ConcurrentHashMap<>();
    }

    /**
     * Register a new candidate
     */
    public void registerCandidate(String id, String name, String party, String manifesto) {
        Candidate candidate = new Candidate.Builder(id, name)
                .party(party)
                .manifesto(manifesto)
                .build();
        
        candidates.putIfAbsent(id, candidate);
    }

    /**
     * Get candidate by ID
     */
    public Candidate getCandidate(String candidateId) throws CandidateNotFoundException {
        Candidate candidate = candidates.get(candidateId);
        if (candidate == null) {
            throw new CandidateNotFoundException(candidateId);
        }
        return candidate;
    }

    /**
     * Create a new election
     */
    public Election createElection(String id, String name, String description,
                                   LocalDateTime startTime, LocalDateTime endTime,
                                   Set<String> candidateIds) {
        // Validate all candidates exist
        for (String candidateId : candidateIds) {
            if (!candidates.containsKey(candidateId)) {
                throw new IllegalArgumentException("Candidate not found: " + candidateId);
            }
        }

        Election election = new Election.Builder(id, name)
                .description(description)
                .startTime(startTime)
                .endTime(endTime)
                .candidateIds(candidateIds)
                .status(ElectionStatus.SCHEDULED)
                .build();
        
        elections.put(id, election);
        return election;
    }

    /**
     * Start an election
     */
    public void startElection(String electionId) {
        Election election = elections.get(electionId);
        ValidationUtil.validateNotNull(election, "Election");
        
        if (!election.isInTimeWindow()) {
            throw new IllegalStateException("Election cannot be started outside its time window");
        }
        
        election.setStatus(ElectionStatus.ONGOING);
    }

    /**
     * End an election
     */
    public void endElection(String electionId) {
        Election election = elections.get(electionId);
        ValidationUtil.validateNotNull(election, "Election");
        
        election.setStatus(ElectionStatus.COMPLETED);
    }

    /**
     * Get election by ID
     */
    public Election getElection(String electionId) {
        return elections.get(electionId);
    }

    /**
     * Get all active elections
     */
    public List<Election> getActiveElections() {
        List<Election> activeElections = new ArrayList<>();
        for (Election election : elections.values()) {
            if (election.isActive()) {
                activeElections.add(election);
            }
        }
        return activeElections;
    }

    /**
     * Get all candidates for an election
     */
    public List<Candidate> getCandidatesForElection(String electionId) {
        Election election = elections.get(electionId);
        if (election == null) {
            return Collections.emptyList();
        }

        List<Candidate> electionCandidates = new ArrayList<>();
        for (String candidateId : election.getCandidateIds()) {
            Candidate candidate = candidates.get(candidateId);
            if (candidate != null) {
                electionCandidates.add(candidate);
            }
        }
        return electionCandidates;
    }

    /**
     * Check if candidate is in election
     */
    public boolean isCandidateInElection(String electionId, String candidateId) {
        Election election = elections.get(electionId);
        return election != null && election.getCandidateIds().contains(candidateId);
    }

    /**
     * Get all candidates
     */
    public List<Candidate> getAllCandidates() {
        return new ArrayList<>(candidates.values());
    }
}
