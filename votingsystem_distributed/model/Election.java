package votingsystem_distributed.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Election entity for distributed system
 */
public class Election {
    private String id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // SCHEDULED, ONGOING, COMPLETED, CANCELLED
    private Set<String> candidateIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Election() {
        this.candidateIds = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Election(String id, String name, String description,
                   LocalDateTime startTime, LocalDateTime endTime, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.candidateIds = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public Set<String> getCandidateIds() { return new HashSet<>(candidateIds); }
    public void setCandidateIds(Set<String> candidateIds) { this.candidateIds = new HashSet<>(candidateIds); }
    public void addCandidateId(String candidateId) { this.candidateIds.add(candidateId); }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public boolean isActive() {
        return "ONGOING".equals(status);
    }

    public boolean isInTimeWindow() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }

    @Override
    public String toString() {
        return "Election{id='" + id + "', name='" + name + "', status='" + status + 
               "', startTime=" + startTime + ", endTime=" + endTime + "}";
    }
}
