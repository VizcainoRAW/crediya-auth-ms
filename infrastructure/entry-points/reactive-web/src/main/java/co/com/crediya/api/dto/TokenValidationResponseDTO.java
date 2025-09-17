package co.com.crediya.api.dto;

public record TokenValidationResponseDTO(
    boolean valid,
    UserDTO user,
    Long expiresIn,
    String message
) {
    public static TokenValidationResponseDTO valid(UserDTO user, Long expiresIn) {
        return new TokenValidationResponseDTO(true, user, expiresIn, "Token is valid");
    }
    
    public static TokenValidationResponseDTO invalid(String message) {
        return new TokenValidationResponseDTO(false, null, null, message);
    }
}
