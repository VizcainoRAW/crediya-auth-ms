package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Error response details")
public record ErrorResponse(
    
    @Schema(description = "Error message", example = "User not found")
    String message,
    
    @Schema(description = "Error code", example = "USER_NOT_FOUND")
    @JsonProperty("error_code") 
    String errorCode,
    
    @Schema(description = "Timestamp when error occurred", example = "2025-01-15T10:30:00")
    LocalDateTime timestamp,
    
    @Schema(description = "Request path where error occurred", example = "/api/users/123")
    String path
) {}