package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request to create a new user")
public record UserDTO(

    String id,
    String firstName,
    String lastName,
    LocalDate birthDate,
    String address,
    String phone,
    String email,
    BigDecimal baseSalary,
    String role,
    String documentType,
    String documentId
) {}