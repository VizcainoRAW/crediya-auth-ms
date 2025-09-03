package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Validation error details")
public record ValidationError(
    
    @Schema(description = "Field that failed validation", example = "email")
    String field,
    
    @Schema(description = "Validation error message", example = "Email must be valid")
    String message,
    
    @Schema(description = "Rejected value", example = "invalid-email")
    Object rejectedValue
) {}