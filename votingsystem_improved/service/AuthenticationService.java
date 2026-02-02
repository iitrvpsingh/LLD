package votingsystem_improved.service;

import votingsystem_improved.exception.InvalidCredentialsException;
import votingsystem_improved.exception.VoterNotFoundException;
import votingsystem_improved.model.Voter;
import votingsystem_improved.util.PasswordHasher;
import votingsystem_improved.util.ValidationUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for handling voter authentication
 * Follows Single Responsibility Principle - only handles authentication
 */
public class AuthenticationService {
    private final Map<String, Voter> voters;

    public AuthenticationService() {
        this.voters = new ConcurrentHashMap<>();
    }

    /**
     * Register a new voter with hashed password
     */
    public void registerVoter(String id, String name, String email, int age, String password) {
        ValidationUtil.validateNotEmpty(password, "Password");
        
        String salt = PasswordHasher.generateSalt();
        String passwordHash = PasswordHasher.hashPassword(password, salt);
        
        Voter voter = new Voter.Builder(id, name)
                .email(email)
                .age(age)
                .passwordHash(passwordHash)
                .salt(salt)
                .build();
        
        voters.putIfAbsent(id, voter);
    }

    /**
     * Authenticate a voter with their credentials
     * @return Voter object if authentication successful
     * @throws VoterNotFoundException if voter doesn't exist
     * @throws InvalidCredentialsException if password is incorrect
     */
    public Voter authenticate(String voterId, String password) 
            throws VoterNotFoundException, InvalidCredentialsException {
        ValidationUtil.validateNotEmpty(voterId, "Voter ID");
        ValidationUtil.validateNotEmpty(password, "Password");
        
        Voter voter = voters.get(voterId);
        if (voter == null) {
            throw new VoterNotFoundException(voterId);
        }
        
        boolean isValid = PasswordHasher.verifyPassword(
            password, 
            voter.getSalt(), 
            voter.getPasswordHash()
        );
        
        if (!isValid) {
            throw new InvalidCredentialsException();
        }
        
        return voter;
    }

    /**
     * Get voter by ID (without authentication)
     */
    public Voter getVoter(String voterId) throws VoterNotFoundException {
        Voter voter = voters.get(voterId);
        if (voter == null) {
            throw new VoterNotFoundException(voterId);
        }
        return voter;
    }

    /**
     * Check if voter exists
     */
    public boolean voterExists(String voterId) {
        return voters.containsKey(voterId);
    }

    /**
     * Get total number of registered voters
     */
    public int getTotalVoters() {
        return voters.size();
    }
}
