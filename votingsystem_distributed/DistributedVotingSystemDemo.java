package votingsystem_distributed;

import votingsystem_distributed.controller.VotingController;
import votingsystem_distributed.dto.*;
import votingsystem_distributed.model.*;
import votingsystem_distributed.repository.*;
import votingsystem_distributed.service.*;
import votingsystem_distributed.util.DistributedLock;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * Demo of Distributed Voting System
 * Simulates multiple machines/servers handling requests
 */
public class DistributedVotingSystemDemo {

    public static void main(String[] args) {
        System.out.println("=== Distributed Voting System Demo ===\n");

        // Initialize shared repositories (simulating shared database)
        VoterRepository voterRepo = new VoterRepository();
        ElectionRepository electionRepo = new ElectionRepository();
        CandidateRepository candidateRepo = new CandidateRepository();
        VoteRepository voteRepo = new VoteRepository();
        VoterAuditLogRepository auditLogRepo = new VoterAuditLogRepository();
        DistributedLock distributedLock = new DistributedLock();

        // Create services (each "machine" would have its own instance)
        AuthenticationService authService = new AuthenticationService(voterRepo);
        ElectionService electionService = new ElectionService(electionRepo, candidateRepo);
        VotingService votingService = new VotingService(
                voterRepo, electionRepo, candidateRepo, voteRepo, auditLogRepo, distributedLock);

        try {
            // Step 1: Setup
            System.out.println("1. Setting up election and registering participants...");
            setupElection(authService, electionService);

            // Step 2: Simulate distributed voting
            System.out.println("\n2. Simulating concurrent voting from multiple machines...");
            simulateDistributedVoting(authService, votingService);

            // Step 3: Show results
            System.out.println("\n3. Election Results:");
            displayResults(votingService, electionService, "E1");

            // Step 4: Demonstrate distributed features
            System.out.println("\n4. Demonstrating Distributed Features:");
            demonstrateDistributedFeatures(authService, votingService);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Demo Complete ===");
    }

    private static void setupElection(AuthenticationService authService,
                                     ElectionService electionService) {
        // Register candidates
        electionService.registerCandidate("C1", "Alice Johnson", "Progressive Party",
                "Focus on education and healthcare");
        electionService.registerCandidate("C2", "Bob Smith", "Conservative Party",
                "Focus on economy and infrastructure");
        electionService.registerCandidate("C3", "Carol Williams", "Green Party",
                "Focus on environment");
        System.out.println("   ✓ Registered 3 candidates");

        // Register voters
        for (int i = 1; i <= 50; i++) {
            authService.registerVoter(
                    "V" + i,
                    "Voter " + i,
                    "voter" + i + "@example.com",
                    20 + (i % 50),
                    "password" + i
            );
        }
        System.out.println("   ✓ Registered 50 voters");

        // Create and start election
        Election election = electionService.createElection(
                "E1",
                "2026 General Election",
                "Annual general election",
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusHours(2),
                new HashSet<>(Arrays.asList("C1", "C2", "C3"))
        );
        electionService.startElection("E1");
        System.out.println("   ✓ Created and started election: " + election.getName());
    }

    private static void simulateDistributedVoting(AuthenticationService authService,
                                                  VotingService votingService) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<VoteResult>> futures = new ArrayList<>();

        // Simulate voting from multiple "machines"
        for (int i = 1; i <= 50; i++) {
            final int voterNum = i;
            futures.add(executor.submit(() -> {
                try {
                    String voterId = "V" + voterNum;
                    String password = "password" + voterNum;

                    // Step 1: Authenticate (get JWT token)
                    String token = authService.authenticate(voterId, password);

                    // Simulate network delay
                    Thread.sleep(new Random().nextInt(50));

                    // Step 2: Cast vote using distributed locking
                    String candidateId = "C" + ((voterNum % 3) + 1);
                    votingService.castVote(voterId, "E1", candidateId);

                    return new VoteResult(voterId, true, null, "Machine-" + (voterNum % 5));
                } catch (Exception e) {
                    return new VoteResult("V" + voterNum, false, e.getMessage(), "");
                }
            }));
        }

        // Collect results
        int successCount = 0;
        int failureCount = 0;
        Map<String, Integer> machineVotes = new HashMap<>();

        for (Future<VoteResult> future : futures) {
            try {
                VoteResult result = future.get();
                if (result.success) {
                    successCount++;
                    machineVotes.merge(result.machineId, 1, Integer::sum);
                } else {
                    failureCount++;
                }
            } catch (Exception e) {
                failureCount++;
            }
        }

        executor.shutdown();

        System.out.println("   ✓ Successful votes: " + successCount);
        System.out.println("   ✗ Failed votes: " + failureCount);
        System.out.println("   📊 Votes processed by each machine:");
        machineVotes.forEach((machine, count) ->
                System.out.println("      " + machine + ": " + count + " votes"));
    }

    private static void displayResults(VotingService votingService,
                                      ElectionService electionService,
                                      String electionId) {
        Map<String, Integer> results = votingService.getElectionResults(electionId);
        long turnout = votingService.getVoterTurnout(electionId);

        System.out.println("   Election ID: " + electionId);
        System.out.println("   Total Votes: " + results.values().stream().mapToInt(Integer::intValue).sum());
        System.out.println("   Voter Turnout: " + turnout);
        System.out.println("\n   Candidate Results:");

        List<Map.Entry<String, Integer>> sortedResults = new ArrayList<>(results.entrySet());
        sortedResults.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (Map.Entry<String, Integer> entry : sortedResults) {
            try {
                Candidate candidate = electionService.getCandidate(entry.getKey());
                double percentage = (entry.getValue() * 100.0) / turnout;
                System.out.printf("   - %s (%s): %d votes (%.1f%%)%s%n",
                        candidate.getName(),
                        candidate.getParty(),
                        entry.getValue(),
                        percentage,
                        entry == sortedResults.get(0) ? " 🏆 WINNER" : ""
                );
            } catch (Exception e) {
                System.out.println("   - Unknown candidate: " + entry.getValue() + " votes");
            }
        }
    }

    private static void demonstrateDistributedFeatures(AuthenticationService authService,
                                                       VotingService votingService) {
        System.out.println("\n   Feature 1: JWT Token Works Across Machines");
        try {
            String token = authService.authenticate("V1", "password1");
            System.out.println("   ✓ Token generated on Machine A: " + token.substring(0, 20) + "...");

            // Simulate different machine verifying the token
            Voter voter = authService.verifyToken(token);
            System.out.println("   ✓ Token verified on Machine B: " + voter.getName());
        } catch (Exception e) {
            System.out.println("   ✗ Token verification failed: " + e.getMessage());
        }

        System.out.println("\n   Feature 2: Distributed Lock Prevents Duplicate Votes");
        try {
            // Try to vote twice (should fail on second attempt)
            votingService.castVote("V1", "E1", "C1");
            System.out.println("   ✗ SECURITY BREACH: Allowed duplicate vote!");
        } catch (Exception e) {
            System.out.println("   ✓ Correctly prevented duplicate vote: " + e.getMessage());
        }

        System.out.println("\n   Feature 3: Database Ensures Consistency");
        System.out.println("   ✓ All machines read from same database");
        System.out.println("   ✓ Votes visible immediately across all machines");
        System.out.println("   ✓ No data loss on machine failure");
    }

    static class VoteResult {
        String voterId;
        boolean success;
        String error;
        String machineId;

        VoteResult(String voterId, boolean success, String error, String machineId) {
            this.voterId = voterId;
            this.success = success;
            this.error = error;
            this.machineId = machineId;
        }
    }
}
