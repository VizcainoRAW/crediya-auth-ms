package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.UserAuthDTO;
import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.model.user.DocumentType;
import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.User;
import co.com.crediya.model.valueobject.BaseSalary;
import co.com.crediya.model.valueobject.DocumentId;
import co.com.crediya.model.valueobject.Email;
import co.com.crediya.model.valueobject.Password;
import co.com.crediya.model.valueobject.ProperName;

public class UserMapper {

    public static User toUser(UserRequestDTO dto) {
        return new User(
            new ProperName(dto.firstName(), "first name"),
            new ProperName(dto.lastName(), "last name"),
            dto.birthDate(),
            dto.address(),
            dto.phone(),
            new Email(dto.email()),         
            new BaseSalary(dto.baseSalary()),
            new Password(dto.password()),
            Role.fromCode(dto.role()),
            dto.documentType() != null ? DocumentType.fromCode(dto.documentType()) : null,
            dto.documentId() != null ? new DocumentId(dto.documentId()) : null
        );
    }

    public static User toUser(UserDTO dto) {
        return new User(
            dto.id(),
            new ProperName(dto.firstName(), "first name"),
            new ProperName(dto.lastName(), "last name"),
            dto.birthDate(),
            dto.address(),
            dto.phone(),
            new Email(dto.email()),         
            new BaseSalary(dto.baseSalary()),
            null, // Password not available in DTO
            dto.role() != null ? Role.fromCode(dto.role()) : Role.USER,
            dto.documentType() != null ? DocumentType.fromCode(dto.documentType()) : null,
            dto.documentId() != null ? new DocumentId(dto.documentId()) : null
        );
    }

    public static UserDTO toDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getFirstName().getValue(),
            user.getLastName().getValue(),
            user.getBirthDate(),
            user.getAddress(),
            user.getPhone(),
            user.getEmail().getValue(),
            user.getBaseSalary().getValue(),
            user.getRole() != null ? user.getRole().getCode() : null,
            user.getDocumentType() != null ? user.getDocumentType().getCode() : null,
            user.getDocumentId() != null ? user.getDocumentId().getMaskedValue() : null // Masked for security
        );
    }

    public static UserAuthDTO toAuthDTO(User user) {
        return new UserAuthDTO(
            user.getId(),
            user.getFirstName().getValue(),
            user.getLastName().getValue(),
            user.getEmail().getValue(),
            user.getRole() != null ? user.getRole().getCode() : null,
            user.getDocumentType() != null ? user.getDocumentType().getCode() : null,
            user.getDocumentId() != null ? user.getDocumentId().getMaskedValue() : null
        );
    }

    public static UserDTO toDTOWithFullDocumentId(User user) {
        return new UserDTO(
            user.getId(),
            user.getFirstName().getValue(),
            user.getLastName().getValue(),
            user.getBirthDate(),
            user.getAddress(),
            user.getPhone(),
            user.getEmail().getValue(),
            user.getBaseSalary().getValue(),
            user.getRole() != null ? user.getRole().getCode() : null,
            user.getDocumentType() != null ? user.getDocumentType().getCode() : null,
            user.getDocumentId() != null ? user.getDocumentId().getValue() : null // Full value for admin
        );
    }
}