# Class Diagram & Relationships

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    VotingSystemFacade                        │
│                  (Singleton + Facade Pattern)                │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ - authService: AuthenticationService                   │ │
│  │ - electionService: ElectionService                     │ │
│  │ - auditService: AuditService                           │ │
│  │ - votingService: VotingService                         │ │
│  │                                                         │ │
│  │ + registerVoter(...)                                   │ │
│  │ + registerCandidate(...)                               │ │
│  │ + createElection(...)                                  │ │
│  │ + castVote(...)                                        │ │
│  │ + getElectionResults(...)                              │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ coordinates
                              ▼
        ┌─────────────────────┴─────────────────────┐
        │                     │                      │
        ▼                     ▼                      ▼
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│ AuthService  │      │ElectionService│      │ AuditService │
├──────────────┤      ├──────────────┤      ├──────────────┤
│- voters: Map │      │- elections   │      │- voterLogs   │
│              │      │- candidates  │      │- voteLogs    │
│+ register()  │      │              │      │              │
│+ authenticate│      │+ create()    │      │+ logVote()   │
└──────────────┘      │+ start()     │      │+ hasVoted()  │
                      │+ end()       │      └──────────────┘
                      └──────────────┘
                              │
                              │ uses all
                              ▼
                      ┌──────────────┐
                      │VotingService │
                      ├──────────────┤
                      │- authService │
                      │- electionSvc │
                      │- auditService│
                      │- votingLock  │
                      │              │
                      │+ castVote()  │
                      │+ getResults()│
                      └──────────────┘
```

---

## Domain Model Classes

```
┌─────────────────────────────────────────────────────────────┐
│                         Voter                                │
│                    (Builder Pattern)                         │
├─────────────────────────────────────────────────────────────┤
│ - id: String (final)                                        │
│ - name: String (final)                                      │
│ - email: String (final)                                     │
│ - age: int (final)                                          │
│ - passwordHash: String (final)                              │
│ - salt: String (final)                                      │
│ - status: VoterStatus                                       │
├─────────────────────────────────────────────────────────────┤
│ + getId(): String                                           │
│ + getName(): String                                         │
│ + getEmail(): String                                        │
│ + getAge(): int                                             │
│ + getPasswordHash(): String                                 │
│ + getSalt(): String                                         │
│ + getStatus(): VoterStatus                                  │
│ + setStatus(VoterStatus): void                              │
│ + isEligible(): boolean                                     │
├─────────────────────────────────────────────────────────────┤
│                    «static nested»                          │
│                      Builder                                │
│  + Builder(id, name)                                        │
│  + email(String): Builder                                   │
│  + age(int): Builder                                        │
│  + passwordHash(String): Builder                            │
│  + salt(String): Builder                                    │
│  + status(VoterStatus): Builder                             │
│  + build(): Voter                                           │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                       Candidate                              │
│                    (Builder Pattern)                         │
├─────────────────────────────────────────────────────────────┤
│ - id: String (final)                                        │
│ - name: String (final)                                      │
│ - party: String (final)                                     │
│ - manifesto: String (final)                                 │
├─────────────────────────────────────────────────────────────┤
│ + getId(): String                                           │
│ + getName(): String                                         │
│ + getParty(): String                                        │
│ + getManifesto(): String                                    │
├─────────────────────────────────────────────────────────────┤
│                    «static nested»                          │
│                      Builder                                │
│  + Builder(id, name)                                        │
│  + party(String): Builder                                   │
│  + manifesto(String): Builder                               │
│  + build(): Candidate                                       │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                       Election                               │
│                    (Builder Pattern)                         │
├─────────────────────────────────────────────────────────────┤
│ - id: String (final)                                        │
│ - name: String (final)                                      │
│ - description: String (final)                               │
│ - startTime: LocalDateTime (final)                          │
│ - endTime: LocalDateTime (final)                            │
│ - candidateIds: Set<String> (final)                         │
│ - status: ElectionStatus                                    │
├─────────────────────────────────────────────────────────────┤
│ + getId(): String                                           │
│ + getName(): String                                         │
│ + getDescription(): String                                  │
│ + getStartTime(): LocalDateTime                             │
│ + getEndTime(): LocalDateTime                               │
│ + getCandidateIds(): Set<String>                            │
│ + getStatus(): ElectionStatus                               │
│ + setStatus(ElectionStatus): void                           │
│ + isActive(): boolean                                       │
│ + isInTimeWindow(): boolean                                 │
│ + addCandidate(String): void                                │
├─────────────────────────────────────────────────────────────┤
│                    «static nested»                          │
│                      Builder                                │
│  + Builder(id, name)                                        │
│  + description(String): Builder                             │
│  + startTime(LocalDateTime): Builder                        │
│  + endTime(LocalDateTime): Builder                          │
│  + candidateIds(Set<String>): Builder                       │
│  + status(ElectionStatus): Builder                          │
│  + build(): Election                                        │
└─────────────────────────────────────────────────────────────┘
```

---

## Value Objects

```
┌─────────────────────────────────────────────────────────────┐
│                          Vote                                │
│                   (Value Object Pattern)                     │
├─────────────────────────────────────────────────────────────┤
│ - voteId: String (final)                                    │
│ - electionId: String (final)                                │
│ - candidateId: String (final)                               │
│ - timestamp: LocalDateTime (final)                          │
├─────────────────────────────────────────────────────────────┤
│ + Vote(electionId, candidateId)                             │
│ + getVoteId(): String                                       │
│ + getElectionId(): String                                   │
│ + getCandidateId(): String                                  │
│ + getTimestamp(): LocalDateTime                             │
│ + equals(Object): boolean                                   │
│ + hashCode(): int                                           │
└─────────────────────────────────────────────────────────────┘
        ▲
        │ NOTE: Does NOT contain voterId
        │       (Ballot Secrecy)

┌─────────────────────────────────────────────────────────────┐
│                    VoterAuditLog                             │
│                   (Value Object Pattern)                     │
├─────────────────────────────────────────────────────────────┤
│ - voterId: String (final)                                   │
│ - electionId: String (final)                                │
│ - timestamp: LocalDateTime (final)                          │
├─────────────────────────────────────────────────────────────┤
│ + VoterAuditLog(voterId, electionId)                        │
│ + getVoterId(): String                                      │
│ + getElectionId(): String                                   │
│ + getTimestamp(): LocalDateTime                             │
│ + equals(Object): boolean                                   │
│ + hashCode(): int                                           │
└─────────────────────────────────────────────────────────────┘
        ▲
        │ NOTE: Does NOT contain candidateId
        │       (Ballot Secrecy)

┌─────────────────────────────────────────────────────────────┐
│                    ElectionResult                            │
│                   (Value Object Pattern)                     │
├─────────────────────────────────────────────────────────────┤
│ - electionId: String (final)                                │
│ - candidateVoteCounts: Map<String, Integer> (final)         │
│ - totalVotes: int (final)                                   │
│ - winnerId: String (final)                                  │
├─────────────────────────────────────────────────────────────┤
│ + ElectionResult(electionId, candidateVoteCounts)           │
│ + getElectionId(): String                                   │
│ + getCandidateVoteCounts(): Map<String, Integer>            │
│ + getTotalVotes(): int                                      │
│ + getWinnerId(): String                                     │
│ + getVotesForCandidate(String): int                         │
└─────────────────────────────────────────────────────────────┘
```

---

## Service Classes

```
┌─────────────────────────────────────────────────────────────┐
│                  AuthenticationService                       │
│                   (Service Layer Pattern)                    │
├─────────────────────────────────────────────────────────────┤
│ - voters: Map<String, Voter>                                │
├─────────────────────────────────────────────────────────────┤
│ + registerVoter(id, name, email, age, password): void       │
│ + authenticate(voterId, password): Voter                    │
│   throws VoterNotFoundException, InvalidCredentialsException│
│ + getVoter(voterId): Voter                                  │
│   throws VoterNotFoundException                             │
│ + voterExists(voterId): boolean                             │
│ + getTotalVoters(): int                                     │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    ElectionService                           │
│                   (Service Layer Pattern)                    │
├─────────────────────────────────────────────────────────────┤
│ - elections: Map<String, Election>                          │
│ - candidates: Map<String, Candidate>                        │
├─────────────────────────────────────────────────────────────┤
│ + registerCandidate(id, name, party, manifesto): void       │
│ + getCandidate(candidateId): Candidate                      │
│   throws CandidateNotFoundException                         │
│ + createElection(...): Election                             │
│ + startElection(electionId): void                           │
│ + endElection(electionId): void                             │
│ + getElection(electionId): Election                         │
│ + getActiveElections(): List<Election>                      │
│ + getCandidatesForElection(electionId): List<Candidate>     │
│ + isCandidateInElection(electionId, candidateId): boolean   │
│ + getAllCandidates(): List<Candidate>                       │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      AuditService                            │
│                   (Service Layer Pattern)                    │
├─────────────────────────────────────────────────────────────┤
│ - voterLogs: Map<String, List<VoterAuditLog>>               │
│ - voteLogs: Map<String, List<Vote>>                         │
├─────────────────────────────────────────────────────────────┤
│ + logVoterParticipation(voterId, electionId): void          │
│ + logVote(vote): void                                       │
│ + hasVoterVoted(voterId, electionId): boolean               │
│ + getVoterLogsForElection(electionId): List<VoterAuditLog>  │
│ + getVotesForElection(electionId): List<Vote>               │
│ + getVoterTurnout(electionId): int                          │
│ + getTotalVotes(electionId): int                            │
│ + getVoteCountByCandidate(electionId): Map<String, Long>    │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                     VotingService                            │
│                   (Service Layer Pattern)                    │
├─────────────────────────────────────────────────────────────┤
│ - authService: AuthenticationService                        │
│ - electionService: ElectionService                          │
│ - auditService: AuditService                                │
│ - votingLock: ReadWriteLock                                 │
├─────────────────────────────────────────────────────────────┤
│ + VotingService(authService, electionService, auditService) │
│ + castVote(voterId, password, electionId, candidateId): void│
│   throws VotingException                                    │
│ + getElectionResults(electionId): ElectionResult            │
│ + getVoterTurnout(electionId): int                          │
│ + hasVoted(voterId, electionId): boolean                    │
└─────────────────────────────────────────────────────────────┘
```

---

## Exception Hierarchy

```
                    ┌─────────────────┐
                    │   Exception     │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │ VotingException │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼──────────┐  ┌──────▼─────────┐  ┌──────▼──────────┐
│VoterNotFoundException│CandidateNotFound│DuplicateVoteException│
└──────────────────┘  └────────────────┘  └─────────────────┘
        │                    │                    │
┌───────▼──────────┐  ┌──────▼─────────┐  ┌──────▼──────────┐
│InvalidCredentials│  │ElectionNotActive│  │VoterIneligible  │
│   Exception      │  │   Exception     │  │   Exception     │
└──────────────────┘  └────────────────┘  └─────────────────┘
```

---

## Enumerations

```
┌─────────────────────┐
│   VoterStatus       │
├─────────────────────┤
│ ELIGIBLE            │
│ INELIGIBLE          │
│ SUSPENDED           │
│ VOTED               │
└─────────────────────┘

┌─────────────────────┐
│  ElectionStatus     │
├─────────────────────┤
│ SCHEDULED           │
│ ONGOING             │
│ COMPLETED           │
│ CANCELLED           │
└─────────────────────┘
```

---

## Utility Classes

```
┌─────────────────────────────────────────────────────────────┐
│                    PasswordHasher                            │
│                   (Utility Class)                            │
├─────────────────────────────────────────────────────────────┤
│ - ALGORITHM: String = "SHA-256"                             │
│ - SALT_LENGTH: int = 16                                     │
├─────────────────────────────────────────────────────────────┤
│ + generateSalt(): String                                    │
│ + hashPassword(password, salt): String                      │
│ + verifyPassword(password, salt, hashedPassword): boolean   │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   ValidationUtil                             │
│                   (Utility Class)                            │
├─────────────────────────────────────────────────────────────┤
│ + validateNotNull(obj, fieldName): void                     │
│ + validateNotEmpty(str, fieldName): void                    │
│ + validatePositive(value, fieldName): void                  │
└─────────────────────────────────────────────────────────────┘
```

---

## Relationships

### Composition (Has-A)

```
VotingSystemFacade ◆──────> AuthenticationService
                   ◆──────> ElectionService
                   ◆──────> AuditService
                   ◆──────> VotingService

VotingService      ◆──────> AuthenticationService
                   ◆──────> ElectionService
                   ◆──────> AuditService
                   ◆──────> ReadWriteLock

AuthenticationService ◆──> Map<String, Voter>
ElectionService       ◆──> Map<String, Election>
                      ◆──> Map<String, Candidate>
AuditService          ◆──> Map<String, List<VoterAuditLog>>
                      ◆──> Map<String, List<Vote>>
```

### Dependency (Uses)

```
VotingService ────uses────> Voter
              ────uses────> Election
              ────uses────> Vote
              ────uses────> VoterAuditLog
              ────uses────> ElectionResult

AuthenticationService ──uses──> PasswordHasher
                      ──uses──> ValidationUtil

Voter.Builder ────uses────> ValidationUtil
Candidate.Builder ─uses────> ValidationUtil
Election.Builder ──uses────> ValidationUtil
```

### Creation (Creates)

```
AuthenticationService ──creates──> Voter
ElectionService ──────creates──> Election
                 ──────creates──> Candidate
VotingService ────────creates──> Vote
AuditService ─────────creates──> VoterAuditLog
                 ─────creates──> ElectionResult
```

---

## Sequence Diagram: Casting a Vote

```
Voter    Facade    VotingService    AuthService    ElectionService    AuditService
  │         │            │                │                │                │
  │ castVote│            │                │                │                │
  ├────────>│            │                │                │                │
  │         │ castVote   │                │                │                │
  │         ├───────────>│                │                │                │
  │         │            │ authenticate   │                │                │
  │         │            ├───────────────>│                │                │
  │         │            │    Voter       │                │                │
  │         │            │<───────────────┤                │                │
  │         │            │                │                │                │
  │         │            │  getElection   │                │                │
  │         │            ├────────────────┼───────────────>│                │
  │         │            │                │   Election     │                │
  │         │            │<───────────────┼────────────────┤                │
  │         │            │                │                │                │
  │         │            │  hasVoterVoted │                │                │
  │         │            ├────────────────┼────────────────┼───────────────>│
  │         │            │                │                │   boolean      │
  │         │            │<───────────────┼────────────────┼────────────────┤
  │         │            │                │                │                │
  │         │            │  logVote       │                │                │
  │         │            ├────────────────┼────────────────┼───────────────>│
  │         │            │                │                │                │
  │         │            │  logVoterParticipation          │                │
  │         │            ├────────────────┼────────────────┼───────────────>│
  │         │            │                │                │                │
  │         │   success  │                │                │                │
  │         │<───────────┤                │                │                │
  │ success │            │                │                │                │
  │<────────┤            │                │                │                │
  │         │            │                │                │                │
```

---

## Package Structure

```
votingsystem/
│
├── model/                          (Domain Layer)
│   ├── Voter.java                  ← Builder Pattern
│   ├── Candidate.java              ← Builder Pattern
│   ├── Election.java               ← Builder Pattern
│   ├── Vote.java                   ← Value Object
│   ├── VoterAuditLog.java          ← Value Object
│   └── ElectionResult.java         ← Value Object
│
├── service/                        (Service Layer)
│   ├── AuthenticationService.java  ← SRP: Authentication
│   ├── VotingService.java          ← SRP: Voting
│   ├── ElectionService.java        ← SRP: Election Management
│   └── AuditService.java           ← SRP: Audit Logging
│
├── exception/                      (Exception Hierarchy)
│   ├── VotingException.java        ← Base Exception
│   ├── VoterNotFoundException.java
│   ├── CandidateNotFoundException.java
│   ├── DuplicateVoteException.java
│   ├── InvalidCredentialsException.java
│   ├── ElectionNotActiveException.java
│   └── VoterIneligibleException.java
│
├── enums/                          (Enumerations)
│   ├── VoterStatus.java
│   └── ElectionStatus.java
│
├── util/                           (Utilities)
│   ├── PasswordHasher.java
│   └── ValidationUtil.java
│
├── VotingSystemFacade.java         ← Facade + Singleton
├── VotingSystemDemo.java           ← Demo
└── Main.java                       ← Entry Point
```

---

## Design Pattern Summary

| Pattern | Location | Purpose |
|---------|----------|---------|
| **Singleton** | VotingSystemFacade | Single system instance |
| **Builder** | Voter, Candidate, Election | Complex object creation |
| **Facade** | VotingSystemFacade | Simplified interface |
| **Service Layer** | All services | Separation of concerns |
| **Value Object** | Vote, VoterAuditLog | Immutable data |

---

## Key Design Decisions

### 1. Ballot Secrecy
```
VoterAuditLog               Vote
┌──────────────┐           ┌──────────────┐
│ voterId      │           │ voteId       │
│ electionId   │           │ electionId   │
│ timestamp    │           │ candidateId  │
│              │           │ timestamp    │
│ NO candidateId│          │ NO voterId   │
└──────────────┘           └──────────────┘
```

### 2. Thread Safety
- `ConcurrentHashMap` for storage
- `ReadWriteLock` for coordination
- Atomic operations for duplicate prevention

### 3. Immutability
- All domain objects immutable after creation
- Builder pattern ensures validation before construction
- Value objects are naturally thread-safe

### 4. Separation of Concerns
- Each service has single responsibility
- Clear boundaries between layers
- Easy to test and maintain

---

## Extensibility Points

### 1. Add New Voting Method
```java
interface VotingStrategy {
    ElectionResult calculate(List<Vote> votes);
}
```

### 2. Add New Authentication
```java
class TwoFactorAuthService extends AuthenticationService {
    // Add 2FA
}
```

### 3. Add New Audit Strategy
```java
class BlockchainAuditService extends AuditService {
    // Add blockchain
}
```

---

This class diagram shows the complete structure of the improved voting system with all relationships, patterns, and design decisions clearly illustrated.
