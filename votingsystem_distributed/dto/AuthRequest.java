package votingsystem_distributed.dto;

/**
 * Data Transfer Object for authentication requests
 */
public class AuthRequest {
    private String voterId;
    private String password;

    public AuthRequest() {}

    public AuthRequest(String voterId, String password) {
        this.voterId = voterId;
        this.password = password;
    }

    public String getVoterId() { return voterId; }
    public void setVoterId(String voterId) { this.voterId = voterId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
