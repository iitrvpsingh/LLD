package votingsystem_distributed.service;

import votingsystem_distributed.model.Candidate;
import votingsystem_distributed.model.Election;
import votingsystem_distributed.repository.CandidateRepository;
import votingsystem_distributed.repository.ElectionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Election management service for distributed system
 */
public class ElectionService {
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;

    public ElectionService(ElectionRepository electionRepository,
                          CandidateRepository candidateRepository) {
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
    }

    /**
     * Register a new candidate
     */
    public void registerCandidate(String id, String name, String party, String manifesto) {
        if (candidateRepository.existsById(id)) {
            throw new IllegalArgumentException("Candidate already exists: " + id);
        }

        Candidate candidate = new Candidate(id, name, party, manifesto);
        candidateRepository.save(candidate);
    }

    /**
     * Get candidate by ID
     */
    public Candidate getCandidate(String candidateId) {
        return candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + candidateId));
    }

    /**
     * Create a new election
     */
    public Election createElection(String id, String name, String description,
                                   LocalDateTime startTime, LocalDateTime endTime,
                                   Set<String> candidateIds) {
        if (electionRepository.existsById(id)) {
            throw new IllegalArgumentException("Election already exists: " + id);
        }

        // Validate all candidates exist
        for (String candidateId : candidateIds) {
            if (!candidateRepository.existsById(candidateId)) {
                throw new IllegalArgumentException("Candidate not found: " + candidateId);
            }
        }

        Election election = new Election(id, name, description, startTime, endTime, "SCHEDULED");
        election.setCandidateIds(candidateIds);
        electionRepository.save(election);
        
        return election;
    }

    /**
     * Start an election
     */
    public void startElection(String electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new IllegalArgumentException("Election not found: " + electionId));

        if (!election.isInTimeWindow()) {
            throw new IllegalStateException("Election cannot be started outside its time window");
        }

        election.setStatus("ONGOING");
        electionRepository.save(election);
    }

    /**
     * End an election
     */
    public void endElection(String electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new IllegalArgumentException("Election not found: " + electionId));

        election.setStatus("COMPLETED");
        electionRepository.save(election);
    }

    /**
     * Get election by ID
     */
    public Election getElection(String electionId) {
        return electionRepository.findById(electionId)
                .orElseThrow(() -> new IllegalArgumentException("Election not found: " + electionId));
    }

    /**
     * Get all active elections
     */
    public List<Election> getActiveElections() {
        return electionRepository.findByStatus("ONGOING");
    }

    /**
     * Get candidates for an election
     */
    public List<Candidate> getCandidatesForElection(String electionId) {
        Election election = getElection(electionId);
        return candidateRepository.findAllById(election.getCandidateIds());
    }
}
