package votingsystem_improved;

import votingsystem_improved.exception.*;
import votingsystem_improved.model.*;
import votingsystem_improved.service.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Facade pattern - Provides a simplified interface to the voting system
 * Coordinates between multiple services
 * Implements Singleton pattern for single system instance
 */
public class VotingSystemFacade {
    private static volatile VotingSystemFacade instance;
    
    private final AuthenticationService authService;
    private final ElectionService electionService;
    private final AuditService auditService;
    private final VotingService votingService;

    private VotingSystemFacade() {
        this.authService = new AuthenticationService();
        this.electionService = new ElectionService();
        this.auditService = new AuditService();
        this.votingService = new VotingService(authService, electionService, auditService);
    }

    /**
     * Thread-safe singleton implementation
     */
    public static VotingSystemFacade getInstance() {
        if (instance == null) {
            synchronized (VotingSystemFacade.class) {
                if (instance == null) {
                    instance = new VotingSystemFacade();
                }
            }
        }
        return instance;
    }

    // ==================== Voter Management ====================

    public void registerVoter(String id, String name, String email, int age, String password) {
        authService.registerVoter(id, name, email, age, password);
    }

    public Voter authenticateVoter(String voterId, String password) 
            throws VoterNotFoundException, InvalidCredentialsException {
        return authService.authenticate(voterId, password);
    }

    // ==================== Candidate Management ====================

    public void registerCandidate(String id, String name, String party, String manifesto) {
        electionService.registerCandidate(id, name, party, manifesto);
    }

    public Candidate getCandidate(String candidateId) throws CandidateNotFoundException {
        return electionService.getCandidate(candidateId);
    }

    public List<Candidate> getAllCandidates() {
        return electionService.getAllCandidates();
    }

    // ==================== Election Management ====================

    public Election createElection(String id, String name, String description,
                                   LocalDateTime startTime, LocalDateTime endTime,
                                   Set<String> candidateIds) {
        return electionService.createElection(id, name, description, startTime, endTime, candidateIds);
    }

    public void startElection(String electionId) {
        electionService.startElection(electionId);
    }

    public void endElection(String electionId) {
        electionService.endElection(electionId);
    }

    public Election getElection(String electionId) {
        return electionService.getElection(electionId);
    }

    public List<Election> getActiveElections() {
        return electionService.getActiveElections();
    }

    public List<Candidate> getCandidatesForElection(String electionId) {
        return electionService.getCandidatesForElection(electionId);
    }

    // ==================== Voting Operations ====================

    public void castVote(String voterId, String password, String electionId, String candidateId) 
            throws VotingException {
        votingService.castVote(voterId, password, electionId, candidateId);
    }

    public boolean hasVoted(String voterId, String electionId) {
        return votingService.hasVoted(voterId, electionId);
    }

    // ==================== Results & Analytics ====================

    public ElectionResult getElectionResults(String electionId) {
        return votingService.getElectionResults(electionId);
    }

    public int getVoterTurnout(String electionId) {
        return votingService.getVoterTurnout(electionId);
    }

    // ==================== Audit ====================

    public List<VoterAuditLog> getVoterParticipationLogs(String electionId) {
        return auditService.getVoterLogsForElection(electionId);
    }

    public List<Vote> getVoteLogs(String electionId) {
        return auditService.getVotesForElection(electionId);
    }
}
