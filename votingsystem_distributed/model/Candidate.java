package votingsystem_distributed.model;

import java.time.LocalDateTime;

/**
 * Candidate entity for distributed system
 */
public class Candidate {
    private String id;
    private String name;
    private String party;
    private String manifesto;
    private LocalDateTime createdAt;

    public Candidate() {
        this.createdAt = LocalDateTime.now();
    }

    public Candidate(String id, String name, String party, String manifesto) {
        this.id = id;
        this.name = name;
        this.party = party;
        this.manifesto = manifesto;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getParty() { return party; }
    public void setParty(String party) { this.party = party; }

    public String getManifesto() { return manifesto; }
    public void setManifesto(String manifesto) { this.manifesto = manifesto; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Candidate{id='" + id + "', name='" + name + "', party='" + party + "'}";
    }
}
