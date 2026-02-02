package votingsystem_distributed.service;

import votingsystem_distributed.model.Voter;
import votingsystem_distributed.repository.VoterRepository;
import votingsystem_distributed.security.JwtUtil;
import votingsystem_distributed.util.PasswordHasher;

/**
 * Authentication service for distributed system
 * Uses JWT for stateless authentication (works across multiple machines)
 */
public class AuthenticationService {
    private final VoterRepository voterRepository;

    public AuthenticationService(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    /**
     * Register a new voter
     */
    public void registerVoter(String id, String name, String email, int age, String password) {
        if (voterRepository.existsById(id)) {
            throw new IllegalArgumentException("Voter already exists: " + id);
        }

        String salt = PasswordHasher.generateSalt();
        String passwordHash = PasswordHasher.hashPassword(password, salt);

        Voter voter = new Voter(id, name, email, age, passwordHash, salt, "ELIGIBLE");
        voterRepository.save(voter);
    }

    /**
     * Authenticate voter and return JWT token
     * Token can be verified by any machine in the cluster
     */
    public String authenticate(String voterId, String password) {
        Voter voter = voterRepository.findById(voterId)
                .orElseThrow(() -> new IllegalArgumentException("Voter not found: " + voterId));

        boolean isValid = PasswordHasher.verifyPassword(
                password,
                voter.getSalt(),
                voter.getPasswordHash()
        );

        if (!isValid) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Generate JWT token (stateless - no session storage needed)
        return JwtUtil.generateToken(voterId);
    }

    /**
     * Verify JWT token and get voter
     * Can be called from any machine
     */
    public Voter verifyToken(String token) {
        if (!JwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        String voterId = JwtUtil.extractVoterId(token);
        return voterRepository.findById(voterId)
                .orElseThrow(() -> new IllegalArgumentException("Voter not found: " + voterId));
    }

    /**
     * Get voter by ID
     */
    public Voter getVoter(String voterId) {
        return voterRepository.findById(voterId)
                .orElseThrow(() -> new IllegalArgumentException("Voter not found: " + voterId));
    }
}
