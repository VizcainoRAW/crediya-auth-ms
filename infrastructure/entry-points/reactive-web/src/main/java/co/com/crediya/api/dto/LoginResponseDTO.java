package co.com.crediya.api.dto;

import java.time.LocalDateTime;

public record LoginResponseDTO(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn,
    UserAuthDTO user,
    LocalDateTime loginTime
) {
    public LoginResponseDTO(String accessToken, String refreshToken, Long expiresIn, UserAuthDTO user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user, LocalDateTime.now());
    }
}
