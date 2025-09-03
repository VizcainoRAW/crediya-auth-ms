package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request to create a new user")
public record UserDTO(

    @Schema(description = "User nacional id", example = "cc 1084672379")
    @Size(min = 3, max = 20, message = "nacional id name must be between 3 and 20 characters")
    String id,
    
    @Schema(description = "User's first name", example = "John", minLength = 3, maxLength = 20)
    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    @JsonProperty("first_name") 
    String firstName,
    
    @Schema(description = "User's last name", example = "Doe") 
    @JsonProperty("last_name")
    String lastName,
    
    @Schema(description = "User's birth date", example = "1990-01-15")
    @JsonProperty("birth_date") 
    LocalDate birthDate,
    
    @Schema(description = "User's address", example = "123 Main St, New York, NY")
    String address,
    
    @Schema(description = "User's phone number", example = "+1-555-123-4567")
    String phone,
    
    @Schema(description = "User's email address", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,
    
    @Schema(description = "User's base salary", example = "75000.00", minimum = "0", maximum = "150000000")
    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.0", message = "Base salary cannot be negative")
    @DecimalMax(value = "150000000.0", message = "Base salary cannot exceed 150,000,000")
    @JsonProperty("base_salary") 
    BigDecimal baseSalary
) {}