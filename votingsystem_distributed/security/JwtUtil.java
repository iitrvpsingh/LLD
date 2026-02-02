package votingsystem_distributed.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token utility for stateless authentication
 * In production: Use libraries like jjwt (io.jsonwebtoken)
 * 
 * Example with jjwt:
 * String token = Jwts.builder()
 *     .setSubject(voterId)
 *     .setIssuedAt(new Date())
 *     .setExpiration(new Date(System.currentTimeMillis() + 3600000))
 *     .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
 *     .compact();
 */
public class JwtUtil {
    private static final String SECRET_KEY = "voting-system-secret-key-change-in-production";
    private static final long EXPIRATION_HOURS = 24;

    /**
     * Generate JWT token for voter
     */
    public static String generateToken(String voterId) {
        try {
            long expirationTime = Instant.now().plus(EXPIRATION_HOURS, ChronoUnit.HOURS)
                    .toEpochMilli();
            
            // Simplified JWT: header.payload.signature
            String header = Base64.getEncoder().encodeToString(
                    "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
            
            String payload = Base64.getEncoder().encodeToString(
                    String.format("{\"sub\":\"%s\",\"exp\":%d}", voterId, expirationTime)
                            .getBytes(StandardCharsets.UTF_8));
            
            String signature = generateSignature(header + "." + payload);
            
            return header + "." + payload + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    /**
     * Extract voter ID from JWT token
     */
    public static String extractVoterId(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid token format");
            }
            
            String payload = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            
            // Simple JSON parsing (in production, use JSON library)
            String voterId = payload.split("\"sub\":\"")[1].split("\"")[0];
            
            return voterId;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
    }

    /**
     * Validate JWT token
     */
    public static boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            
            // Verify signature
            String expectedSignature = generateSignature(parts[0] + "." + parts[1]);
            if (!expectedSignature.equals(parts[2])) {
                return false;
            }
            
            // Check expiration
            String payload = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            String expStr = payload.split("\"exp\":")[1].split("}")[0];
            long expiration = Long.parseLong(expStr);
            
            return Instant.now().toEpochMilli() < expiration;
        } catch (Exception e) {
            return false;
        }
    }

    private static String generateSignature(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((data + SECRET_KEY).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }

    /**
     * Parse token and return claims
     */
    public static Map<String, String> parseToken(String token) {
        Map<String, String> claims = new HashMap<>();
        
        if (!validateToken(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        
        claims.put("voterId", extractVoterId(token));
        return claims;
    }
}
