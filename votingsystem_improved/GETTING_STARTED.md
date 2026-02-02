# Getting Started - Improved Voting System

## Quick Start (5 minutes)

### 1. Compile the System
```bash
cd /Users/vvishwanathsingh/Downloads/awesome-low-level-design/solutions/java/src
javac votingsystem_improved/**/*.java votingsystem_improved/*.java
```

### 2. Run the Demo
```bash
java votingsystem_improved.Main
```

### 3. Expected Output
```
=== Improved Voting System Demo ===

1. Registering Candidates...
   ✓ Registered 3 candidates

2. Registering Voters...
   ✓ Registered 100 voters

3. Creating Election...
   ✓ Created election: 2026 General Election

4. Starting Election...
   ✓ Election is now ACTIVE

5. Simulating Concurrent Voting (100 voters)...
   ✓ Successful votes: 100
   ✗ Failed votes: 0

6. Ending Election...
   ✓ Election has ENDED

7. Election Results:
   [Results with winner]

8. Testing Security Features:
   ✓ Correctly rejected wrong password
   ✓ Correctly prevented duplicate voting
   ✓ Correctly rejected non-existent voter

9. Demonstrating Ballot Secrecy:
   ✓ Ballot secrecy maintained!

=== Demo Complete ===
```

---

## Project Structure

```
votingsystem_improved/
│
├── 📄 README.md                    ← Start here! Overview & features
├── 📄 GETTING_STARTED.md           ← This file
├── 📄 DESIGN_PATTERNS.md           ← Deep dive into patterns
├── 📄 INTERVIEW_CHEATSHEET.md      ← Quick reference for interviews
├── 📄 CLASS_DIAGRAM.md             ← Visual architecture
│
├── 📁 model/                       ← Domain entities
│   ├── Voter.java                  (Builder pattern)
│   ├── Candidate.java              (Builder pattern)
│   ├── Election.java               (Builder pattern)
│   ├── Vote.java                   (Value object)
│   ├── VoterAuditLog.java          (Value object)
│   └── ElectionResult.java         (Value object)
│
├── 📁 service/                     ← Business logic
│   ├── AuthenticationService.java  (Authentication)
│   ├── VotingService.java          (Vote casting)
│   ├── ElectionService.java        (Election management)
│   └── AuditService.java           (Audit logging)
│
├── 📁 exception/                   ← Custom exceptions
│   ├── VotingException.java        (Base exception)
│   ├── VoterNotFoundException.java
│   ├── CandidateNotFoundException.java
│   ├── DuplicateVoteException.java
│   ├── InvalidCredentialsException.java
│   ├── ElectionNotActiveException.java
│   └── VoterIneligibleException.java
│
├── 📁 enums/                       ← Enumerations
│   ├── VoterStatus.java
│   └── ElectionStatus.java
│
├── 📁 util/                        ← Utilities
│   ├── PasswordHasher.java
│   └── ValidationUtil.java
│
├── VotingSystemFacade.java         ← Main interface (Facade + Singleton)
├── VotingSystemDemo.java           ← Demo application
└── Main.java                       ← Entry point
```

---

## Reading Guide

### For Interview Preparation (30 minutes)

**Step 1: Overview (5 min)**
- Read `README.md` - Understand the system

**Step 2: Quick Reference (10 min)**
- Read `INTERVIEW_CHEATSHEET.md` - Key talking points

**Step 3: Deep Dive (15 min)**
- Read `DESIGN_PATTERNS.md` - Understand design decisions
- Read `CLASS_DIAGRAM.md` - Visualize architecture

**Step 4: Practice (5 min)**
- Run the demo
- Modify and experiment

### For Understanding Design Patterns (1 hour)

**Step 1: Read Theory**
- `DESIGN_PATTERNS.md` - All patterns explained

**Step 2: See in Code**
- `model/Voter.java` - Builder pattern
- `VotingSystemFacade.java` - Singleton + Facade
- `service/*.java` - Service layer pattern

**Step 3: Understand Relationships**
- `CLASS_DIAGRAM.md` - How classes interact

### For Learning SOLID Principles (45 minutes)

**Step 1: Read Principles**
- `DESIGN_PATTERNS.md` - SOLID section

**Step 2: See in Code**
- **SRP**: Each service has one responsibility
- **OCP**: Easy to extend (Strategy pattern example)
- **LSP**: Exception hierarchy
- **ISP**: Focused service interfaces
- **DIP**: Service dependencies

**Step 3: Identify Violations**
- Compare with original `votingsystem/` folder
- See what was improved

---

## Code Examples

### Example 1: Register and Vote

```java
VotingSystemFacade system = VotingSystemFacade.getInstance();

// Register a voter
system.registerVoter(
    "V1",                    // ID
    "Alice Johnson",         // Name
    "alice@example.com",     // Email
    25,                      // Age
    "securePassword123"      // Password (will be hashed)
);

// Register a candidate
system.registerCandidate(
    "C1",                    // ID
    "Bob Smith",             // Name
    "Progressive Party",     // Party
    "My vision for change"   // Manifesto
);

// Create an election
Election election = system.createElection(
    "E1",                                    // ID
    "2026 General Election",                 // Name
    "Annual leadership election",            // Description
    LocalDateTime.now(),                     // Start time
    LocalDateTime.now().plusHours(2),        // End time
    Set.of("C1", "C2", "C3")                // Candidate IDs
);

// Start the election
system.startElection("E1");

// Cast a vote (with authentication)
try {
    system.castVote(
        "V1",                // Voter ID
        "securePassword123", // Password
        "E1",                // Election ID
        "C1"                 // Candidate ID
    );
    System.out.println("Vote cast successfully!");
} catch (VotingException e) {
    System.err.println("Vote failed: " + e.getMessage());
}

// Get results
ElectionResult result = system.getElectionResults("E1");
System.out.println("Winner: " + result.getWinnerId());
System.out.println("Total votes: " + result.getTotalVotes());
```

### Example 2: Using Builder Pattern

```java
// Create a voter with Builder
Voter voter = new Voter.Builder("V1", "Alice Johnson")
    .email("alice@example.com")
    .age(25)
    .passwordHash(hash)
    .salt(salt)
    .status(VoterStatus.ELIGIBLE)
    .build();

// Create a candidate with Builder
Candidate candidate = new Candidate.Builder("C1", "Bob Smith")
    .party("Progressive Party")
    .manifesto("My vision for the future")
    .build();

// Create an election with Builder
Election election = new Election.Builder("E1", "General Election")
    .description("Annual election")
    .startTime(LocalDateTime.now())
    .endTime(LocalDateTime.now().plusHours(2))
    .candidateIds(Set.of("C1", "C2"))
    .status(ElectionStatus.SCHEDULED)
    .build();
```

### Example 3: Exception Handling

```java
try {
    system.castVote("V1", "password", "E1", "C1");
} catch (VoterNotFoundException e) {
    System.err.println("Voter not found: " + e.getMessage());
} catch (InvalidCredentialsException e) {
    System.err.println("Wrong password: " + e.getMessage());
} catch (DuplicateVoteException e) {
    System.err.println("Already voted: " + e.getMessage());
} catch (ElectionNotActiveException e) {
    System.err.println("Election not active: " + e.getMessage());
} catch (VotingException e) {
    System.err.println("Voting error: " + e.getMessage());
}
```

---

## Key Features Demonstrated

### ✅ Security
- **Password Hashing**: SHA-256 with salt
- **Authentication**: Required before voting
- **Ballot Secrecy**: Separate audit logs
- **Input Validation**: Comprehensive checks

### ✅ Design Patterns
- **Singleton**: VotingSystemFacade
- **Builder**: Voter, Candidate, Election
- **Facade**: Simplified interface
- **Service Layer**: Separation of concerns
- **Value Object**: Vote, VoterAuditLog

### ✅ SOLID Principles
- **SRP**: Each service has one responsibility
- **OCP**: Easy to extend
- **LSP**: Exception hierarchy
- **ISP**: Focused interfaces
- **DIP**: Service dependencies

### ✅ Thread Safety
- **ConcurrentHashMap**: Thread-safe storage
- **ReadWriteLock**: Coordinated access
- **Atomic Operations**: Duplicate prevention

### ✅ Extensibility
- Easy to add new voting methods
- Easy to add new authentication methods
- Easy to add new audit strategies

---

## Common Tasks

### Task 1: Add a New Voter
```java
system.registerVoter("V101", "New Voter", "new@example.com", 30, "password");
```

### Task 2: Add a New Candidate
```java
system.registerCandidate("C4", "New Candidate", "New Party", "My manifesto");
```

### Task 3: Create a New Election
```java
Election election = system.createElection(
    "E2",
    "Special Election",
    "Special election for specific purpose",
    LocalDateTime.now(),
    LocalDateTime.now().plusDays(1),
    Set.of("C1", "C2", "C3", "C4")
);
```

### Task 4: Check if Voter Has Voted
```java
boolean hasVoted = system.hasVoted("V1", "E1");
System.out.println("Has voted: " + hasVoted);
```

### Task 5: Get Election Results
```java
ElectionResult result = system.getElectionResults("E1");
Map<String, Integer> voteCounts = result.getCandidateVoteCounts();

for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
    String candidateId = entry.getKey();
    int votes = entry.getValue();
    Candidate candidate = system.getCandidate(candidateId);
    System.out.println(candidate.getName() + ": " + votes + " votes");
}
```

### Task 6: Get Voter Turnout
```java
int turnout = system.getVoterTurnout("E1");
System.out.println("Voter turnout: " + turnout);
```

---

## Troubleshooting

### Issue: Compilation Errors
**Solution**: Make sure you're in the correct directory
```bash
cd /Users/vvishwanathsingh/Downloads/awesome-low-level-design/solutions/java/src
```

### Issue: ClassNotFoundException
**Solution**: Compile all files first
```bash
javac votingsystem_improved/**/*.java votingsystem_improved/*.java
```

### Issue: VotingException - Election not active
**Solution**: Start the election first
```java
system.startElection("E1");
```

### Issue: DuplicateVoteException
**Solution**: This is expected behavior - voters can only vote once

### Issue: InvalidCredentialsException
**Solution**: Check the password is correct

---

## Extending the System

### Extension 1: Add Ranked Choice Voting

```java
// 1. Create interface
public interface VotingStrategy {
    ElectionResult calculateResults(List<Vote> votes, List<Candidate> candidates);
}

// 2. Implement strategy
public class RankedChoiceStrategy implements VotingStrategy {
    @Override
    public ElectionResult calculateResults(List<Vote> votes, List<Candidate> candidates) {
        // Implement ranked choice algorithm
    }
}

// 3. Add to Election
public class Election {
    private VotingStrategy strategy;
    
    public void setVotingStrategy(VotingStrategy strategy) {
        this.strategy = strategy;
    }
}
```

### Extension 2: Add Two-Factor Authentication

```java
public class TwoFactorAuthService extends AuthenticationService {
    private Map<String, String> totpSecrets = new ConcurrentHashMap<>();
    
    public void enableTwoFactor(String voterId, String secret) {
        totpSecrets.put(voterId, secret);
    }
    
    public Voter authenticateWithTOTP(String voterId, String password, String code) 
            throws VotingException {
        Voter voter = super.authenticate(voterId, password);
        
        if (totpSecrets.containsKey(voterId)) {
            if (!verifyTOTP(voterId, code)) {
                throw new InvalidCredentialsException();
            }
        }
        
        return voter;
    }
    
    private boolean verifyTOTP(String voterId, String code) {
        // Implement TOTP verification
        return true;
    }
}
```

### Extension 3: Add Database Persistence

```java
public interface VoterRepository {
    void save(Voter voter);
    Optional<Voter> findById(String id);
    List<Voter> findAll();
}

public class JpaVoterRepository implements VoterRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public void save(Voter voter) {
        entityManager.persist(voter);
    }
    
    @Override
    public Optional<Voter> findById(String id) {
        return Optional.ofNullable(entityManager.find(Voter.class, id));
    }
    
    @Override
    public List<Voter> findAll() {
        return entityManager.createQuery("SELECT v FROM Voter v", Voter.class)
            .getResultList();
    }
}
```

---

## Testing

### Unit Test Example

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {
    @Test
    void testPasswordHashing() {
        String password = "testPassword123";
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hashPassword(password, salt);
        
        assertTrue(PasswordHasher.verifyPassword(password, salt, hash));
        assertFalse(PasswordHasher.verifyPassword("wrongPassword", salt, hash));
    }
}

class VotingServiceTest {
    @Test
    void testDuplicateVotePrevention() {
        VotingSystemFacade system = VotingSystemFacade.getInstance();
        
        // Setup
        system.registerVoter("V1", "Test", "test@example.com", 25, "password");
        system.registerCandidate("C1", "Candidate", "Party", "Manifesto");
        Election election = system.createElection("E1", "Test", "", 
            LocalDateTime.now(), LocalDateTime.now().plusHours(1), Set.of("C1"));
        system.startElection("E1");
        
        // First vote should succeed
        assertDoesNotThrow(() -> system.castVote("V1", "password", "E1", "C1"));
        
        // Second vote should fail
        assertThrows(DuplicateVoteException.class, () -> 
            system.castVote("V1", "password", "E1", "C1"));
    }
}
```

---

## Performance Considerations

### Current Performance
- **Vote Casting**: O(1) with lock overhead
- **Result Retrieval**: O(n) where n = number of votes
- **Voter Lookup**: O(1) HashMap lookup
- **Duplicate Check**: O(1) Set lookup

### Bottlenecks
1. Write lock is exclusive (only one vote at a time)
2. In-memory storage (limited by RAM)
3. No caching of results

### Optimization Strategies
1. **Sharding**: Shard by election ID
2. **Caching**: Cache election results
3. **Async Processing**: Process votes asynchronously
4. **Database Indexing**: Index on voterId, electionId

---

## Next Steps

1. **Study the Code**
   - Read through each class
   - Understand the relationships
   - Trace the vote casting flow

2. **Run the Demo**
   - See it in action
   - Modify parameters
   - Add your own test cases

3. **Practice Explaining**
   - Use the cheatsheet
   - Practice the 30-second overview
   - Practice the 5-minute deep dive

4. **Extend the System**
   - Add a new feature
   - Implement a suggested extension
   - Create your own enhancement

5. **Prepare for Interview**
   - Review design patterns
   - Review SOLID principles
   - Practice answering questions

---

## Resources

- **README.md**: Complete overview and features
- **DESIGN_PATTERNS.md**: Deep dive into patterns and principles
- **INTERVIEW_CHEATSHEET.md**: Quick reference for interviews
- **CLASS_DIAGRAM.md**: Visual architecture and relationships

---

## Support

For questions or clarifications:
1. Read the documentation files
2. Review the code comments
3. Run the demo to see it in action
4. Experiment with modifications

---

**Good luck with your LLD interview preparation!** 🚀

This improved voting system demonstrates enterprise-level software engineering practices and is perfect for showcasing your understanding of design patterns, SOLID principles, and clean code architecture.
