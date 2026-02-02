package votingsystem_improved.model;

import votingsystem_improved.enums.VoterStatus;
import votingsystem_improved.util.ValidationUtil;

import java.util.Objects;

/**
 * Represents a voter in the system
 * Follows Single Responsibility Principle - only manages voter data
 */
public class Voter {
    private final String id;
    private final String name;
    private final String email;
    private final int age;
    private final String passwordHash;
    private final String salt;
    private VoterStatus status;

    private Voter(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
        this.passwordHash = builder.passwordHash;
        this.salt = builder.salt;
        this.status = builder.status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public VoterStatus getStatus() {
        return status;
    }

    public void setStatus(VoterStatus status) {
        this.status = status;
    }

    public boolean isEligible() {
        return status == VoterStatus.ELIGIBLE && age >= 18;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voter voter = (Voter) o;
        return Objects.equals(id, voter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Voter{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", status=" + status +
                '}';
    }

    /**
     * Builder pattern for creating Voter objects
     */
    public static class Builder {
        private final String id;
        private final String name;
        private String email;
        private int age;
        private String passwordHash;
        private String salt;
        private VoterStatus status = VoterStatus.ELIGIBLE;

        public Builder(String id, String name) {
            ValidationUtil.validateNotEmpty(id, "Voter ID");
            ValidationUtil.validateNotEmpty(name, "Voter name");
            this.id = id;
            this.name = name;
        }

        public Builder email(String email) {
            ValidationUtil.validateNotEmpty(email, "Email");
            this.email = email;
            return this;
        }

        public Builder age(int age) {
            ValidationUtil.validatePositive(age, "Age");
            this.age = age;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder salt(String salt) {
            this.salt = salt;
            return this;
        }

        public Builder status(VoterStatus status) {
            this.status = status;
            return this;
        }

        public Voter build() {
            ValidationUtil.validateNotEmpty(email, "Email");
            ValidationUtil.validatePositive(age, "Age");
            ValidationUtil.validateNotEmpty(passwordHash, "Password hash");
            ValidationUtil.validateNotEmpty(salt, "Salt");
            return new Voter(this);
        }
    }
}
