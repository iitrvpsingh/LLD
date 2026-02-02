package votingsystem_distributed.controller;

import votingsystem_distributed.dto.*;
import votingsystem_distributed.model.Voter;
import votingsystem_distributed.service.*;

import java.util.Map;

/**
 * REST API Controller for Voting System
 * 
 * In production with Spring Boot:
 * @RestController
 * @RequestMapping("/api/v1/voting")
 * public class VotingController {
 *     @Autowired
 *     private VotingService votingService;
 *     
 *     @PostMapping("/vote")
 *     public ResponseEntity<String> castVote(
 *         @RequestHeader("Authorization") String token,
 *         @RequestBody VoteRequest request) {
 *         // Implementation
 *     }
 * }
 */
public class VotingController {
    private final AuthenticationService authService;
    private final VotingService votingService;
    private final ElectionService electionService;

    public VotingController(AuthenticationService authService,
                           VotingService votingService,
                           ElectionService electionService) {
        this.authService = authService;
        this.votingService = votingService;
        this.electionService = electionService;
    }

    /**
     * POST /api/auth/login
     * Authenticate and get JWT token
     */
    public AuthResponse login(AuthRequest request) {
        try {
            String token = authService.authenticate(request.getVoterId(), request.getPassword());
            return new AuthResponse(token, request.getVoterId(), "Authentication successful");
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * POST /api/vote
     * Cast a vote (requires JWT token in header)
     * 
     * Example request:
     * POST /api/vote
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     * {
     *   "electionId": "E1",
     *   "candidateId": "C1",
     *   "idempotencyKey": "unique-key-123"
     * }
     */
    public String castVote(String authToken, VoteRequest request) {
        try {
            // Extract token from "Bearer <token>" format
            String token = authToken.replace("Bearer ", "");
            
            // Verify token and get voter
            Voter voter = authService.verifyToken(token);
            
            // Cast vote with distributed locking
            votingService.castVote(voter.getId(), request.getElectionId(), request.getCandidateId());
            
            return "Vote cast successfully";
        } catch (Exception e) {
            throw new RuntimeException("Vote failed: " + e.getMessage());
        }
    }

    /**
     * GET /api/results/{electionId}
     * Get election results (public endpoint)
     */
    public Map<String, Integer> getResults(String electionId) {
        return votingService.getElectionResults(electionId);
    }

    /**
     * GET /api/turnout/{electionId}
     * Get voter turnout
     */
    public long getTurnout(String electionId) {
        return votingService.getVoterTurnout(electionId);
    }

    /**
     * GET /api/elections/active
     * Get all active elections
     */
    public Object getActiveElections() {
        return electionService.getActiveElections();
    }
}
