package votingsystem_improved.model;

import votingsystem_improved.enums.ElectionStatus;
import votingsystem_improved.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an election
 * Follows Single Responsibility Principle - manages election data
 */
public class Election {
    private final String id;
    private final String name;
    private final String description;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Set<String> candidateIds;
    private ElectionStatus status;

    private Election(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.candidateIds = new HashSet<>(builder.candidateIds);
        this.status = builder.status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Set<String> getCandidateIds() {
        return Collections.unmodifiableSet(candidateIds);
    }

    public ElectionStatus getStatus() {
        return status;
    }

    public void setStatus(ElectionStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return status == ElectionStatus.ONGOING;
    }

    public boolean isInTimeWindow() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }

    public void addCandidate(String candidateId) {
        candidateIds.add(candidateId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Election election = (Election) o;
        return Objects.equals(id, election.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Election{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    /**
     * Builder pattern for creating Election objects
     */
    public static class Builder {
        private final String id;
        private final String name;
        private String description = "";
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Set<String> candidateIds = new HashSet<>();
        private ElectionStatus status = ElectionStatus.SCHEDULED;

        public Builder(String id, String name) {
            ValidationUtil.validateNotEmpty(id, "Election ID");
            ValidationUtil.validateNotEmpty(name, "Election name");
            this.id = id;
            this.name = name;
        }

        public Builder description(String description) {
            this.description = description != null ? description : "";
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            ValidationUtil.validateNotNull(startTime, "Start time");
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalDateTime endTime) {
            ValidationUtil.validateNotNull(endTime, "End time");
            this.endTime = endTime;
            return this;
        }

        public Builder candidateIds(Set<String> candidateIds) {
            if (candidateIds != null) {
                this.candidateIds = new HashSet<>(candidateIds);
            }
            return this;
        }

        public Builder status(ElectionStatus status) {
            this.status = status;
            return this;
        }

        public Election build() {
            ValidationUtil.validateNotNull(startTime, "Start time");
            ValidationUtil.validateNotNull(endTime, "End time");
            if (endTime.isBefore(startTime)) {
                throw new IllegalArgumentException("End time must be after start time");
            }
            return new Election(this);
        }
    }
}
