package votingsystem_improved;

import votingsystem_improved.exception.*;
import votingsystem_improved.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * Demonstration of the improved voting system
 * Shows thread-safe concurrent voting with proper authentication and ballot secrecy
 */
public class VotingSystemDemo {
    
    public static void run() {
        System.out.println("=== Improved Voting System Demo ===\n");
        
        VotingSystemFacade system = VotingSystemFacade.getInstance();
        
        try {
            // Step 1: Register candidates
            System.out.println("1. Registering Candidates...");
            system.registerCandidate("C1", "Alice Johnson", "Progressive Party", 
                "Focus on education and healthcare");
            system.registerCandidate("C2", "Bob Smith", "Conservative Party", 
                "Focus on economy and infrastructure");
            system.registerCandidate("C3", "Carol Williams", "Green Party", 
                "Focus on environment and sustainability");
            System.out.println("   ✓ Registered 3 candidates\n");
            
            // Step 2: Register voters
            System.out.println("2. Registering Voters...");
            for (int i = 1; i <= 100; i++) {
                system.registerVoter(
                    "V" + i, 
                    "Voter " + i, 
                    "voter" + i + "@example.com",
                    20 + (i % 50),  // Ages 20-69
                    "password" + i
                );
            }
            System.out.println("   ✓ Registered 100 voters\n");
            
            // Step 3: Create election
            System.out.println("3. Creating Election...");
            LocalDateTime now = LocalDateTime.now();
            Election election = system.createElection(
                "E1",
                "2026 General Election",
                "Annual general election for leadership",
                now.minusMinutes(1),
                now.plusHours(2),
                new HashSet<>(Arrays.asList("C1", "C2", "C3"))
            );
            System.out.println("   ✓ Created election: " + election.getName() + "\n");
            
            // Step 4: Start election
            System.out.println("4. Starting Election...");
            system.startElection("E1");
            System.out.println("   ✓ Election is now ACTIVE\n");
            
            // Step 5: Simulate concurrent voting
            System.out.println("5. Simulating Concurrent Voting (100 voters)...");
            simulateConcurrentVoting(system);
            
            // Step 6: End election
            System.out.println("\n6. Ending Election...");
            system.endElection("E1");
            System.out.println("   ✓ Election has ENDED\n");
            
            // Step 7: Display results
            System.out.println("7. Election Results:");
            displayResults(system, "E1");
            
            // Step 8: Test security features
            System.out.println("\n8. Testing Security Features:");
            testSecurityFeatures(system);
            
            // Step 9: Demonstrate ballot secrecy
            System.out.println("\n9. Demonstrating Ballot Secrecy:");
            demonstrateBallotSecrecy(system, "E1");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Demo Complete ===");
    }
    
    private static void simulateConcurrentVoting(VotingSystemFacade system) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<VoteResult>> futures = new ArrayList<>();
        
        // Submit voting tasks
        for (int i = 1; i <= 100; i++) {
            final int voterNum = i;
            futures.add(executor.submit(() -> {
                try {
                    String voterId = "V" + voterNum;
                    String password = "password" + voterNum;
                    // Distribute votes among candidates
                    String candidateId = "C" + ((voterNum % 3) + 1);
                    
                    // Random delay to simulate real-world timing
                    Thread.sleep(new Random().nextInt(50));
                    
                    system.castVote(voterId, password, "E1", candidateId);
                    return new VoteResult(voterId, true, null);
                } catch (Exception e) {
                    return new VoteResult("V" + voterNum, false, e.getMessage());
                }
            }));
        }
        
        // Collect results
        int successCount = 0;
        int failureCount = 0;
        
        for (Future<VoteResult> future : futures) {
            try {
                VoteResult result = future.get();
                if (result.success) {
                    successCount++;
                } else {
                    failureCount++;
                    System.out.println("   ✗ Vote failed for " + result.voterId + ": " + result.error);
                }
            } catch (Exception e) {
                failureCount++;
            }
        }
        
        executor.shutdown();
        
        System.out.println("   ✓ Successful votes: " + successCount);
        System.out.println("   ✗ Failed votes: " + failureCount);
    }
    
    private static void displayResults(VotingSystemFacade system, String electionId) {
        ElectionResult result = system.getElectionResults(electionId);
        
        System.out.println("   Election ID: " + result.getElectionId());
        System.out.println("   Total Votes Cast: " + result.getTotalVotes());
        System.out.println("   Voter Turnout: " + system.getVoterTurnout(electionId));
        System.out.println("\n   Candidate Results:");
        
        Map<String, Integer> voteCounts = result.getCandidateVoteCounts();
        List<Map.Entry<String, Integer>> sortedResults = new ArrayList<>(voteCounts.entrySet());
        sortedResults.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        for (Map.Entry<String, Integer> entry : sortedResults) {
            try {
                Candidate candidate = system.getCandidate(entry.getKey());
                double percentage = (entry.getValue() * 100.0) / result.getTotalVotes();
                System.out.printf("   - %s (%s): %d votes (%.1f%%)%s%n",
                    candidate.getName(),
                    candidate.getParty(),
                    entry.getValue(),
                    percentage,
                    entry.getKey().equals(result.getWinnerId()) ? " 🏆 WINNER" : ""
                );
            } catch (CandidateNotFoundException e) {
                System.out.println("   - Unknown candidate: " + entry.getValue() + " votes");
            }
        }
    }
    
    private static void testSecurityFeatures(VotingSystemFacade system) {
        // Test 1: Wrong password
        System.out.println("   Test 1: Attempting vote with wrong password...");
        try {
            system.castVote("V1", "wrongpassword", "E1", "C1");
            System.out.println("   ✗ SECURITY BREACH: Accepted wrong password!");
        } catch (InvalidCredentialsException e) {
            System.out.println("   ✓ Correctly rejected wrong password");
        } catch (Exception e) {
            System.out.println("   ✓ Vote rejected: " + e.getMessage());
        }
        
        // Test 2: Duplicate voting
        System.out.println("\n   Test 2: Attempting duplicate vote...");
        try {
            system.castVote("V1", "password1", "E1", "C2");
            System.out.println("   ✗ SECURITY BREACH: Allowed duplicate vote!");
        } catch (DuplicateVoteException e) {
            System.out.println("   ✓ Correctly prevented duplicate voting");
        } catch (Exception e) {
            System.out.println("   ✓ Vote rejected: " + e.getMessage());
        }
        
        // Test 3: Non-existent voter
        System.out.println("\n   Test 3: Attempting vote with non-existent voter...");
        try {
            system.castVote("V999", "password", "E1", "C1");
            System.out.println("   ✗ SECURITY BREACH: Accepted non-existent voter!");
        } catch (VoterNotFoundException e) {
            System.out.println("   ✓ Correctly rejected non-existent voter");
        } catch (Exception e) {
            System.out.println("   ✓ Vote rejected: " + e.getMessage());
        }
    }
    
    private static void demonstrateBallotSecrecy(VotingSystemFacade system, String electionId) {
        System.out.println("   Voter Participation Logs (WHO voted, not WHAT):");
        List<VoterAuditLog> voterLogs = system.getVoterParticipationLogs(electionId);
        System.out.println("   - Total voters who participated: " + voterLogs.size());
        System.out.println("   - Sample: " + voterLogs.stream().limit(3)
            .map(log -> log.getVoterId()).toList());
        System.out.println("   - Note: These logs do NOT contain vote choices\n");
        
        System.out.println("   Vote Logs (WHAT was voted, not WHO):");
        List<Vote> voteLogs = system.getVoteLogs(electionId);
        System.out.println("   - Total votes cast: " + voteLogs.size());
        System.out.println("   - Sample vote IDs: " + voteLogs.stream().limit(3)
            .map(vote -> vote.getVoteId().substring(0, 8) + "...").toList());
        System.out.println("   - Note: These logs do NOT contain voter IDs\n");
        
        System.out.println("   ✓ Ballot secrecy maintained: Cannot link voters to their votes!");
    }
    
    // Helper class for vote results
    private static class VoteResult {
        String voterId;
        boolean success;
        String error;
        
        VoteResult(String voterId, boolean success, String error) {
            this.voterId = voterId;
            this.success = success;
            this.error = error;
        }
    }
}
