package co.com.crediya.r2dbc.helper;

import org.springframework.stereotype.Component;

import co.com.crediya.model.user.User;
import co.com.crediya.model.valueobject.BaseSalary;
import co.com.crediya.model.valueobject.Email;
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
            // Create value objects with validation
            ProperName firstName = new ProperName(entity.getFirstName(), "first name");
            ProperName lastName = new ProperName(entity.getLastName(), "last name");
            Email email = new Email(entity.getEmail());
            BaseSalary baseSalary = new BaseSalary(entity.getBaseSalary());
            
            return User.builder()
                    .id(entity.getId())
                    .firstName(firstName)
                    .lastName(lastName)
                    .birthDate(entity.getBirthDate())
                    .address(entity.getAddress())
                    .phone(entity.getPhone())
                    .email(email)
                    .baseSalary(baseSalary)
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
        entity.setId(user.getId());
        entity.setFirstName(user.getFirstName().getValue());
        entity.setLastName(user.getLastName().getValue());
        entity.setBirthDate(user.getBirthDate());
        entity.setAddress(user.getAddress());
        entity.setPhone(user.getPhone());
        entity.setEmail(user.getEmail().getValue());
        entity.setBaseSalary(user.getBaseSalary().getValue());
        
        return entity;
    }
}