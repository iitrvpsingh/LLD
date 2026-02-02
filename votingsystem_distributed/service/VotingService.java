package votingsystem_distributed.service;

import votingsystem_distributed.model.*;
import votingsystem_distributed.repository.*;
import votingsystem_distributed.util.DistributedLock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Voting service for distributed system
 * Uses distributed locking to prevent race conditions across machines
 */
public class VotingService {
    private final VoterRepository voterRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;
    private final VoterAuditLogRepository auditLogRepository;
    private final DistributedLock distributedLock;

    public VotingService(VoterRepository voterRepository,
                        ElectionRepository electionRepository,
                        CandidateRepository candidateRepository,
                        VoteRepository voteRepository,
                        VoterAuditLogRepository auditLogRepository,
                        DistributedLock distributedLock) {
        this.voterRepository = voterRepository;
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
        this.voteRepository = voteRepository;
        this.auditLogRepository = auditLogRepository;
        this.distributedLock = distributedLock;
    }

    /**
     * Cast vote with distributed locking
     * Prevents duplicate votes even when running on multiple machines
     */
    public void castVote(String voterId, String electionId, String candidateId) {
        // Create distributed lock key for this voter-election combination
        String lockKey = "vote_lock:" + voterId + ":" + electionId;

        // Try to acquire distributed lock (works across all machines)
        if (!distributedLock.tryLock(lockKey, 5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Could not acquire lock for voting");
        }

        try {
            // Step 1: Validate voter
            Voter voter = voterRepository.findById(voterId)
                    .orElseThrow(() -> new IllegalArgumentException("Voter not found: " + voterId));

            if (!voter.isEligible()) {
                throw new IllegalArgumentException("Voter is not eligible: " + voterId);
            }

            // Step 2: Validate election
            Election election = electionRepository.findById(electionId)
                    .orElseThrow(() -> new IllegalArgumentException("Election not found: " + electionId));

            if (!election.isActive()) {
                throw new IllegalArgumentException("Election is not active: " + electionId);
            }

            // Step 3: Validate candidate
            if (!candidateRepository.existsById(candidateId)) {
                throw new IllegalArgumentException("Candidate not found: " + candidateId);
            }

            if (!election.getCandidateIds().contains(candidateId)) {
                throw new IllegalArgumentException("Candidate not in this election: " + candidateId);
            }

            // Step 4: Check duplicate voting (database enforces unique constraint)
            if (auditLogRepository.existsByVoterIdAndElectionId(voterId, electionId)) {
                throw new IllegalStateException("Voter already voted: " + voterId);
            }

            // Step 5: Record vote (without voter ID - ballot secrecy)
            Vote vote = new Vote(electionId, candidateId);
            voteRepository.save(vote);

            // Step 6: Log voter participation (without candidate ID - ballot secrecy)
            VoterAuditLog auditLog = new VoterAuditLog(voterId, electionId);
            auditLogRepository.save(auditLog); // Will throw if duplicate (DB constraint)

            // Step 7: Update voter status
            voter.setStatus("VOTED");
            voterRepository.save(voter);

        } finally {
            // Always release the distributed lock
            distributedLock.unlock(lockKey);
        }
    }

    /**
     * Get election results
     * Can be called from any machine
     */
    public Map<String, Integer> getElectionResults(String electionId) {
        Map<String, Long> voteCounts = voteRepository.countByCandidateForElection(electionId);
        
        Map<String, Integer> results = new HashMap<>();
        for (Map.Entry<String, Long> entry : voteCounts.entrySet()) {
            results.put(entry.getKey(), entry.getValue().intValue());
        }
        
        return results;
    }

    /**
     * Get voter turnout
     */
    public long getVoterTurnout(String electionId) {
        return auditLogRepository.countByElectionId(electionId);
    }

    /**
     * Check if voter has voted
     */
    public boolean hasVoted(String voterId, String electionId) {
        return auditLogRepository.existsByVoterIdAndElectionId(voterId, electionId);
    }
}
