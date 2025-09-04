package co.com.crediya.model.user;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Objects;

import co.com.crediya.model.valueobject.BaseSalary;
import co.com.crediya.model.valueobject.DocumentId;
import co.com.crediya.model.valueobject.Email;
import co.com.crediya.model.valueobject.Password;
import co.com.crediya.model.valueobject.ProperName;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class User {
    private String id;
    private final ProperName firstName;
    private final ProperName lastName;
    private LocalDate birthDate;
    private String address;
    private String phone;
    private final Email email;
    private final BaseSalary baseSalary;

    private final Password password;
    private final Role role;
    private final DocumentType documentType;
    private final DocumentId documentId;

    public User(String id, ProperName firstName, ProperName lastName,
                LocalDate birthDate, String address, String phone,
                Email email, BaseSalary baseSalary, Password password,
                Role role, DocumentType documentType, DocumentId documentId) {
        
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.baseSalary = baseSalary;
        this.password = password;
        this.role = role;
        this.documentType = documentType;
        this.documentId = documentId;
    }

    public User(ProperName firstName, ProperName lastName,
                LocalDate birthDate, String address, String phone,
                Email email, BaseSalary baseSalary, Password password,
                Role role, DocumentType documentType, DocumentId documentId) {
        
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.baseSalary = baseSalary;
        this.password = password;
        this.role = role;
        this.documentType = documentType;
        this.documentId = documentId;
    }

    public User(String id, ProperName firstName, ProperName lastName, 
                Email email, BaseSalary baseSalary, Password password, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.baseSalary = baseSalary;
        this.password = password;
        this.role = role;
        this.documentType = null;
        this.documentId = null;
    }

    public User(ProperName firstName, ProperName lastName, 
                Email email, BaseSalary baseSalary, Password password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.baseSalary = baseSalary;
        this.password = password;
        this.role = role;
        this.documentType = null;
        this.documentId = null;
    }

    public String getFullName() {
        return firstName.getValue() + " " + lastName.getValue();
    }

    public boolean hasAdminRole() {
        return role == Role.ADMIN;
    }

    public boolean hasDocument() {
        return documentType != null && documentId != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(email, user.email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name=" + getFullName() +
                ", email=" + email +
                ", role=" + role +
                ", documentType=" + documentType +
                '}';
    }
}
