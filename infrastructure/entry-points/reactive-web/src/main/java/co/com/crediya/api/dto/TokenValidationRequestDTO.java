package co.com.crediya.api.dto;


public record TokenValidationRequestDTO(
        String token,
        String service
) {}