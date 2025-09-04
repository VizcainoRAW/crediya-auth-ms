package co.com.crediya.api.dto;

public record UserAuthDTO(
    String id,
    String firstName,
    String lastName,
    String fullName,
    String email,
    String role,
    String documentType,
    String maskedDocumentId
) {
    public UserAuthDTO(String id, String firstName, String lastName, String email, 
                      String role, String documentType, String maskedDocumentId) {
        this(id, firstName, lastName, firstName + " " + lastName, email, role, documentType, maskedDocumentId);
    }
}