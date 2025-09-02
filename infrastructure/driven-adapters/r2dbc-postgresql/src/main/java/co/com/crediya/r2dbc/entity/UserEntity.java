package co.com.crediya.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder(toBuilder = true)
@Table("users")
public class UserEntity {
    
    @Id
    private String id;
    
    @Column("first_name")
    private String firstName;
    
    @Column("last_name")
    private String lastName;
    
    @Column("birth_date")
    private LocalDate birthDate;
    
    @Column("address")
    private String address;
    
    @Column("phone")
    private String phone;
    
    @Column("email")
    private String email;
    
    @Column("base_salary")
    private BigDecimal baseSalary;
    
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    // Default constructor required by R2DBC
    public UserEntity() {}

    // Constructor with all fields
    public UserEntity(String id, String firstName, String lastName, LocalDate birthDate,
                     String address, String phone, String email, BigDecimal baseSalary,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.baseSalary = baseSalary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Factory method for creating new entities
    public static UserEntity create(String id, String firstName, String lastName, LocalDate birthDate,
                                   String address, String phone, String email, BigDecimal baseSalary) {
        LocalDateTime now = LocalDateTime.now();
        return new UserEntity(id, firstName, lastName, birthDate, address, phone, email, baseSalary, now, now);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}