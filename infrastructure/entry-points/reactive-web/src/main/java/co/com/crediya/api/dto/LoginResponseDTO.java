package co.com.crediya.api.dto;

import java.time.LocalDateTime;

public record LoginResponseDTO(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn,
    LocalDateTime loginTime
) {
    public LoginResponseDTO(String accessToken, String refreshToken, Long expiresIn) {
        this(accessToken, refreshToken, "Bearer", expiresIn, LocalDateTime.now());
    }
}
