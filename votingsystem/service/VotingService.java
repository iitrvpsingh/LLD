package votingsystem.service;

import votingsystem.enums.VoterStatus;
import votingsystem.exception.*;
import votingsystem.model.*;
import votingsystem.util.ValidationUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Service for handling voting operations
 * Follows Single Responsibility Principle - only handles vote casting
 * Thread-safe implementation for concurrent voting
 */
public class VotingService {
    private final AuthenticationService authService;
    private final ElectionService electionService;
    private final AuditService auditService;
    private final ReadWriteLock votingLock;

    public VotingService(AuthenticationService authService, 
                        ElectionService electionService,
                        AuditService auditService) {
        this.authService = authService;
        this.electionService = electionService;
        this.auditService = auditService;
        this.votingLock = new ReentrantReadWriteLock();
    }

    /**
     * Cast a vote in an election
     * Ensures: authentication, eligibility, no duplicate voting, ballot secrecy
     * 
     * @param voterId ID of the voter
     * @param password Voter's password for authentication
     * @param electionId ID of the election
     * @param candidateId ID of the candidate to vote for
     * @throws VotingException if vote cannot be cast
     */
    public void castVote(String voterId, String password, String electionId, String candidateId) 
            throws VotingException {
        ValidationUtil.validateNotEmpty(voterId, "Voter ID");
        ValidationUtil.validateNotEmpty(password, "Password");
        ValidationUtil.validateNotEmpty(electionId, "Election ID");
        ValidationUtil.validateNotEmpty(candidateId, "Candidate ID");

        votingLock.writeLock().lock();
        try {
            // Step 1: Authenticate voter
            Voter voter = authService.authenticate(voterId, password);

            // Step 2: Check voter eligibility
            if (!voter.isEligible()) {
                throw new VoterIneligibleException(voterId);
            }

            // Step 3: Validate election exists and is active
            Election election = electionService.getElection(electionId);
            if (election == null) {
                throw new VotingException("Election not found: " + electionId);
            }
            if (!election.isActive()) {
                throw new ElectionNotActiveException(electionId);
            }

            // Step 4: Validate candidate is in this election
            if (!electionService.isCandidateInElection(electionId, candidateId)) {
                throw new CandidateNotFoundException(candidateId);
            }

            // Step 5: Check for duplicate voting
            if (auditService.hasVoterVoted(voterId, electionId)) {
                throw new DuplicateVoteException(voterId);
            }

            // Step 6: Create and record the vote (without voter ID for secrecy)
            Vote vote = new Vote(electionId, candidateId);
            auditService.logVote(vote);

            // Step 7: Log voter participation (without vote choice)
            auditService.logVoterParticipation(voterId, electionId);

            // Step 8: Update voter status
            voter.setStatus(VoterStatus.VOTED);

        } finally {
            votingLock.writeLock().unlock();
        }
    }

    /**
     * Get election results
     * Can be called during or after election
     */
    public ElectionResult getElectionResults(String electionId) {
        votingLock.readLock().lock();
        try {
            Map<String, Long> voteCounts = auditService.getVoteCountByCandidate(electionId);
            
            // Convert Long to Integer for ElectionResult
            Map<String, Integer> intVoteCounts = new HashMap<>();
            for (Map.Entry<String, Long> entry : voteCounts.entrySet()) {
                intVoteCounts.put(entry.getKey(), entry.getValue().intValue());
            }
            
            return new ElectionResult(electionId, intVoteCounts);
        } finally {
            votingLock.readLock().unlock();
        }
    }

    /**
     * Get voter turnout for an election
     */
    public int getVoterTurnout(String electionId) {
        return auditService.getVoterTurnout(electionId);
    }

    /**
     * Check if a voter has voted in an election
     */
    public boolean hasVoted(String voterId, String electionId) {
        return auditService.hasVoterVoted(voterId, electionId);
    }
}
