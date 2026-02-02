package votingsystem_improved.model;

import votingsystem_improved.util.ValidationUtil;

import java.util.Objects;

/**
 * Represents a candidate in an election
 * Immutable class following value object pattern
 */
public class Candidate {
    private final String id;
    private final String name;
    private final String party;
    private final String manifesto;

    private Candidate(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.party = builder.party;
        this.manifesto = builder.manifesto;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public String getManifesto() {
        return manifesto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return Objects.equals(id, candidate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", party='" + party + '\'' +
                '}';
    }

    /**
     * Builder pattern for creating Candidate objects
     */
    public static class Builder {
        private final String id;
        private final String name;
        private String party;
        private String manifesto = "";

        public Builder(String id, String name) {
            ValidationUtil.validateNotEmpty(id, "Candidate ID");
            ValidationUtil.validateNotEmpty(name, "Candidate name");
            this.id = id;
            this.name = name;
        }

        public Builder party(String party) {
            ValidationUtil.validateNotEmpty(party, "Party");
            this.party = party;
            return this;
        }

        public Builder manifesto(String manifesto) {
            this.manifesto = manifesto != null ? manifesto : "";
            return this;
        }

        public Candidate build() {
            ValidationUtil.validateNotEmpty(party, "Party");
            return new Candidate(this);
        }
    }
}
