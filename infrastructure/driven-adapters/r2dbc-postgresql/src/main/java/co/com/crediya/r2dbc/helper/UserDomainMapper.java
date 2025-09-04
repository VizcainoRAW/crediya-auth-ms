package co.com.crediya.r2dbc.helper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import co.com.crediya.model.user.DocumentType;
import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.User;
import co.com.crediya.model.valueobject.BaseSalary;
import co.com.crediya.model.valueobject.DocumentId;
import co.com.crediya.model.valueobject.Email;
import co.com.crediya.model.valueobject.Password;
import co.com.crediya.model.valueobject.ProperName;
import co.com.crediya.r2dbc.entity.UserEntity;

@Component
public class UserDomainMapper {
    
    /**
     * Converts UserEntity to Domain User
     * Handles value object construction and validation
     */
    public User entityToDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        try {
            ProperName firstName = new ProperName(entity.getFirstName(), "first name");
            ProperName lastName = new ProperName(entity.getLastName(), "last name");
            Email email = new Email(entity.getEmail());
            BaseSalary baseSalary = new BaseSalary(entity.getBaseSalary());
            
            Password password = entity.getPasswordHash() != null ? 
                Password.fromHash(entity.getPasswordHash()) : null;
            Role role = entity.getRole() != null ? 
                Role.fromCode(entity.getRole()) : Role.USER;
            DocumentType documentType = entity.getDocumentType() != null ? 
                DocumentType.fromCode(entity.getDocumentType()) : null;
            DocumentId documentId = entity.getDocumentId() != null ? 
                new DocumentId(entity.getDocumentId()) : null;
            
            return User.builder()
                    .id(entity.getId().toString())
                    .firstName(firstName)
                    .lastName(lastName)
                    .birthDate(entity.getBirthDate())
                    .address(entity.getAddress())
                    .phone(entity.getPhone())
                    .email(email)
                    .baseSalary(baseSalary)
                    .password(password)
                    .role(role)
                    .documentType(documentType)
                    .documentId(documentId)
                    .build();
                    
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid data in database for user ID: " + entity.getId(), e);
        }
    }
    
    /**
     * Converts Domain User to UserEntity
     * Extracts values from value objects
     */
    public UserEntity domainToEntity(User user) {
        if (user == null) {
            return null;
        }
        
        UserEntity entity = new UserEntity();
        entity.setId(user.getId() != null ? UUID.fromString(user.getId()) : null);
        entity.setFirstName(user.getFirstName().getValue());
        entity.setLastName(user.getLastName().getValue());
        entity.setBirthDate(user.getBirthDate());
        entity.setAddress(user.getAddress());
        entity.setPhone(user.getPhone());
        entity.setEmail(user.getEmail().getValue());
        entity.setBaseSalary(user.getBaseSalary().getValue());
        
        entity.setPasswordHash(user.getPassword() != null ? user.getPassword().getValue() : null);
        entity.setRole(user.getRole() != null ? user.getRole().getCode() : null);
        entity.setDocumentType(user.getDocumentType() != null ? user.getDocumentType().getCode() : null);
        entity.setDocumentId(user.getDocumentId() != null ? user.getDocumentId().getValue() : null);
        
        return entity;
    }
}