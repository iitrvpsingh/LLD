package votingsystem_distributed.model;

import java.time.LocalDateTime;

/**
 * Voter entity for distributed system
 * Designed to be persisted in database
 */
public class Voter {
    private String id;
    private String name;
    private String email;
    private int age;
    private String passwordHash;
    private String salt;
    private String status; // ELIGIBLE, VOTED, SUSPENDED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor for ORM frameworks (JPA, Hibernate)
    public Voter() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Voter(String id, String name, String email, int age, 
                 String passwordHash, String salt, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isEligible() {
        return "ELIGIBLE".equals(status) && age >= 18;
    }

    @Override
    public String toString() {
        return "Voter{id='" + id + "', name='" + name + "', email='" + email + 
               "', age=" + age + ", status='" + status + "'}";
    }
}
