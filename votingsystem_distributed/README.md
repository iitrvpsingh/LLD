# Distributed Voting System

## Overview

This is a **distributed, scalable voting system** designed to run across multiple machines/servers. Unlike the single-machine version, this implementation addresses the challenges of distributed computing:

- ✅ **Stateless services** - No in-memory state, works across multiple servers
- ✅ **JWT authentication** - Token-based auth that works on any machine
- ✅ **Distributed locking** - Prevents race conditions across servers
- ✅ **Database-backed** - Shared data store for consistency
- ✅ **REST API ready** - Can be deployed as microservices
- ✅ **Ballot secrecy maintained** - Even in distributed environment

---

## Key Differences from Single-Machine Version

| Aspect | Single Machine | Distributed |
|--------|---------------|-------------|
| **Storage** | ConcurrentHashMap (in-memory) | Database repositories |
| **Singleton** | getInstance() | Dependency Injection |
| **Authentication** | In-memory session | JWT tokens |
| **Locking** | ReentrantReadWriteLock | Distributed locks (Redis-like) |
| **State** | Stateful (memory) | Stateless (database) |
| **Scalability** | Vertical only | Horizontal scaling |
| **Fault Tolerance** | Single point of failure | Multiple servers |

---

## Architecture

```
                    ┌──────────────┐
                    │ Load Balancer│
                    └──────┬───────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   ┌────▼────┐       ┌────▼────┐       ┌────▼────┐
   │Server A │       │Server B │       │Server C │
   │(Stateless)      │(Stateless)      │(Stateless)
   └────┬────┘       └────┬────┘       └────┬────┘
        │                  │                  │
        └──────────────────┼──────────────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   ┌────▼────┐       ┌────▼────┐       ┌────▼────┐
   │Database │       │  Redis  │       │  Cache  │
   │(Shared) │       │ (Locks) │       │(Optional)│
   └─────────┘       └─────────┘       └─────────┘
```

---

## Package Structure

```
votingsystem_distributed/
├── model/                      # Domain entities (DB-ready)
│   ├── Voter.java
│   ├── Candidate.java
│   ├── Election.java
│   ├── Vote.java
│   └── VoterAuditLog.java
│
├── repository/                 # Data access layer (replaces in-memory)
│   ├── VoterRepository.java
│   ├── ElectionRepository.java
│   ├── CandidateRepository.java
│   ├── VoteRepository.java
│   └── VoterAuditLogRepository.java
│
├── service/                    # Business logic (stateless)
│   ├── AuthenticationService.java
│   ├── VotingService.java
│   └── ElectionService.java
│
├── controller/                 # REST API endpoints
│   └── VotingController.java
│
├── dto/                        # Data Transfer Objects
│   ├── VoteRequest.java
│   ├── AuthRequest.java
│   └── AuthResponse.java
│
├── security/                   # Security utilities
│   └── JwtUtil.java
│
├── util/                       # Utilities
│   ├── DistributedLock.java
│   └── PasswordHasher.java
│
└── DistributedVotingSystemDemo.java
```

---

## Key Features

### 1. **JWT-Based Authentication** 🔐

**Problem**: Session state doesn't work across multiple servers

**Solution**: Stateless JWT tokens

```java
// User logs in on Server A
String token = authService.authenticate("V1", "password");
// Returns: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

// User makes request to Server B (different machine)
Voter voter = authService.verifyToken(token);
// Works! Token is self-contained
```

**Benefits**:
- No session storage needed
- Works on any server
- Scalable

---

### 2. **Distributed Locking** 🔒

**Problem**: Race conditions when multiple servers process votes simultaneously

**Solution**: Distributed locks (simulates Redis Redlock)

```java
String lockKey = "vote_lock:" + voterId + ":" + electionId;

if (distributedLock.tryLock(lockKey, 5, TimeUnit.SECONDS)) {
    try {
        // Only ONE server can execute this at a time
        // Even if 100 servers try simultaneously
        castVote(...);
    } finally {
        distributedLock.unlock(lockKey);
    }
}
```

**Prevents**:
- Duplicate votes
- Race conditions
- Data corruption

---

### 3. **Database-Backed Storage** 💾

**Problem**: In-memory data lost on server restart, not shared across servers

**Solution**: Repository pattern with database

```java
// All servers read/write to same database
voterRepository.save(voter);        // Server A writes
voter = voterRepository.findById(id); // Server B reads
```

**Benefits**:
- Data persistence
- Shared across all servers
- ACID transactions
- Fault tolerance

---

### 4. **Ballot Secrecy in Distributed System** 🤐

**Maintained through**:
- Separate tables: `votes` (no voterId) and `voter_audit_logs` (no candidateId)
- Database constraints prevent linking
- Vote mixing (optional)

```sql
-- votes table (WHAT was voted)
CREATE TABLE votes (
    vote_id VARCHAR PRIMARY KEY,
    election_id VARCHAR NOT NULL,
    candidate_id VARCHAR NOT NULL,
    timestamp TIMESTAMP
    -- NO voter_id column!
);

-- voter_audit_logs table (WHO voted)
CREATE TABLE voter_audit_logs (
    voter_id VARCHAR NOT NULL,
    election_id VARCHAR NOT NULL,
    timestamp TIMESTAMP,
    PRIMARY KEY (voter_id, election_id)
    -- NO candidate_id column!
);
```

---

## How It Works

### Voting Flow

```
1. Client → Server A: POST /api/auth/login
   ↓
2. Server A → Database: Verify credentials
   ↓
3. Server A → Client: Return JWT token
   ↓
4. Client → Server B: POST /api/vote (with JWT token)
   ↓
5. Server B: Verify JWT (no database call needed!)
   ↓
6. Server B: Acquire distributed lock
   ↓
7. Server B → Database: Check if already voted
   ↓
8. Server B → Database: Save vote + audit log (transaction)
   ↓
9. Server B: Release distributed lock
   ↓
10. Server B → Client: Success response
```

---

## Running the Demo

### Compile
```bash
cd ~/Downloads/LLD
javac votingsystem_distributed/**/*.java votingsystem_distributed/*.java
```

### Run
```bash
java votingsystem_distributed.DistributedVotingSystemDemo
```

### Expected Output
```
=== Distributed Voting System Demo ===

1. Setting up election and registering participants...
   ✓ Registered 3 candidates
   ✓ Registered 50 voters
   ✓ Created and started election: 2026 General Election

2. Simulating concurrent voting from multiple machines...
   ✓ Successful votes: 50
   ✗ Failed votes: 0
   📊 Votes processed by each machine:
      Machine-0: 10 votes
      Machine-1: 10 votes
      Machine-2: 10 votes
      Machine-3: 10 votes
      Machine-4: 10 votes

3. Election Results:
   [Results with winner]

4. Demonstrating Distributed Features:
   ✓ JWT Token Works Across Machines
   ✓ Distributed Lock Prevents Duplicate Votes
   ✓ Database Ensures Consistency

=== Demo Complete ===
```

---

## Production Deployment

### Technology Stack

**Database**:
```yaml
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/voting_db
spring.jpa.hibernate.ddl-auto=update
```

**Redis (Distributed Locks)**:
```yaml
spring.redis.host=localhost
spring.redis.port=6379
```

**Load Balancer**:
```nginx
upstream voting_servers {
    server server1:8080;
    server server2:8080;
    server server3:8080;
}

server {
    listen 80;
    location / {
        proxy_pass http://voting_servers;
    }
}
```

---

## API Endpoints

### Authentication
```http
POST /api/auth/login
Content-Type: application/json

{
  "voterId": "V1",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "voterId": "V1",
  "message": "Authentication successful"
}
```

### Cast Vote
```http
POST /api/vote
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "electionId": "E1",
  "candidateId": "C1",
  "idempotencyKey": "unique-key-123"
}

Response:
"Vote cast successfully"
```

### Get Results
```http
GET /api/results/E1

Response:
{
  "C1": 150,
  "C2": 120,
  "C3": 80
}
```

---

## Handling Distributed Challenges

### 1. **Network Failures**

**Problem**: Request sent, network fails, client retries → duplicate vote?

**Solution**: Idempotency keys

```java
// Client generates unique key
String idempotencyKey = UUID.randomUUID().toString();

// Server checks if already processed
if (processedRequests.contains(idempotencyKey)) {
    return "Already processed";
}

// Process vote
castVote(...);

// Mark as processed
processedRequests.add(idempotencyKey);
```

### 2. **Clock Skew**

**Problem**: Different servers have different times

**Solution**: Use database timestamps

```java
// Don't use: LocalDateTime.now() on application server
// Use: Database-generated timestamps
@Column(name = "created_at", insertable = false, updatable = false,
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
private LocalDateTime createdAt;
```

### 3. **Split Brain**

**Problem**: Network partition causes two groups of servers

**Solution**: Quorum-based decisions (Redis Redlock)

```java
// Acquire lock from majority of Redis nodes
RLock lock1 = redisson1.getLock("vote_lock");
RLock lock2 = redisson2.getLock("vote_lock");
RLock lock3 = redisson3.getLock("vote_lock");

RedissonMultiLock multiLock = new RedissonMultiLock(lock1, lock2, lock3);
multiLock.lock(); // Requires majority (2 out of 3)
```

---

## Scaling Strategy

### Horizontal Scaling

```bash
# Start multiple instances
java -jar voting-service.jar --server.port=8080 &
java -jar voting-service.jar --server.port=8081 &
java -jar voting-service.jar --server.port=8082 &
```

### Database Scaling

**Read Replicas**:
```
Master (writes) → Replica 1 (reads)
                → Replica 2 (reads)
                → Replica 3 (reads)
```

**Sharding** (for massive scale):
```
Shard 1: Elections E1-E1000
Shard 2: Elections E1001-E2000
Shard 3: Elections E2001-E3000
```

---

## Monitoring & Observability

### Metrics to Track
- Request latency per endpoint
- Vote processing time
- Lock acquisition time
- Database query time
- Error rates
- Active users
- Votes per second

### Distributed Tracing
```java
// Add trace ID to each request
String traceId = UUID.randomUUID().toString();
MDC.put("traceId", traceId);

logger.info("Processing vote"); // Automatically includes traceId
```

---

## Interview Discussion Points

### Q: How does this handle server failures?

**A**: 
1. **Stateless servers** - Any server can handle any request
2. **Database persistence** - Data survives server crashes
3. **Load balancer** - Automatically routes around failed servers
4. **No single point of failure** - Multiple servers running

### Q: How do you prevent duplicate votes?

**A**:
1. **Distributed locks** - Only one server processes vote at a time
2. **Database unique constraint** - (voterId, electionId) must be unique
3. **Idempotency keys** - Detect network retries
4. **Transaction isolation** - ACID guarantees

### Q: How does JWT improve scalability?

**A**:
- **No session storage** - Don't need to share sessions across servers
- **Self-contained** - Token has all info needed
- **Stateless** - Servers don't need to remember anything
- **Fast** - No database lookup for every request

### Q: What if Redis (lock server) fails?

**A**:
- **Use Redis Cluster** - Multiple Redis nodes
- **Redlock algorithm** - Acquire locks from majority
- **Fallback** - Can use database locks (slower but works)

---

## Comparison: Single vs Distributed

| Feature | Single Machine | Distributed |
|---------|---------------|-------------|
| **Throughput** | Limited by one CPU | Scales linearly with servers |
| **Availability** | 99% (single server) | 99.99% (multiple servers) |
| **Data Loss Risk** | High (memory only) | Low (database + replicas) |
| **Complexity** | Simple | More complex |
| **Cost** | Low | Higher (multiple servers) |
| **When to Use** | Small elections | Large-scale elections |

---

## Next Steps

1. **Add actual database** - Replace simulated repos with JPA/Hibernate
2. **Add Redis** - Replace simulated locks with Redisson
3. **Add Spring Boot** - Convert to REST API
4. **Add monitoring** - Prometheus + Grafana
5. **Add caching** - Redis for election results
6. **Add message queue** - Kafka for event-driven architecture

---

## Summary

This distributed voting system demonstrates:

✅ **Stateless architecture** for horizontal scaling  
✅ **JWT authentication** for distributed auth  
✅ **Distributed locking** for consistency  
✅ **Database-backed** for persistence  
✅ **REST API ready** for microservices  
✅ **Ballot secrecy** maintained in distributed environment  
✅ **Production-ready patterns** for real-world deployment  

**Perfect for demonstrating distributed systems knowledge in interviews!** 🚀
