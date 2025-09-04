package co.com.crediya.api.dto;

public record TokenValidationResponseDTO(
    boolean valid,
    String userId,
    String email,
    String role,
    Long expiresIn,
    String message
) {
    public static TokenValidationResponseDTO valid(String userId, String email, String role, Long expiresIn) {
        return new TokenValidationResponseDTO(true, userId, email, role, expiresIn, "Token is valid");
    }
    
    public static TokenValidationResponseDTO invalid(String message) {
        return new TokenValidationResponseDTO(false, null, null, null, null, message);
    }
}
