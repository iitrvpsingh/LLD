package votingsystem_distributed.dto;

/**
 * Data Transfer Object for authentication response
 */
public class AuthResponse {
    private String token;
    private String voterId;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, String voterId, String message) {
        this.token = token;
        this.voterId = voterId;
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getVoterId() { return voterId; }
    public void setVoterId(String voterId) { this.voterId = voterId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
