package co.com.crediya.model.user;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Objects;

import co.com.crediya.model.valueobject.BaseSalary;
import co.com.crediya.model.valueobject.Email;
import co.com.crediya.model.valueobject.ProperName;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class User {
    private String id;
    private final ProperName firstName;
    private final ProperName lastName;
    private  LocalDate birthDate;
    private  String address;
    private  String phone;
    private final Email email;
    private final BaseSalary baseSalary;


    public User(String id, ProperName firstName, ProperName lastName,
                       LocalDate birthDate, String address,
                       String phone, Email email, BaseSalary baseSalary) {
        
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate=birthDate;
        this.address=address;
        this.phone=phone;
        this.email = email;
        this.baseSalary = baseSalary;
    }

    public User(ProperName firstName, ProperName lastName,
                       LocalDate birthDate, String address,
                       String phone, Email email, BaseSalary baseSalary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate=birthDate;
        this.address=address;
        this.phone=phone;
        this.email = email;
        this.baseSalary = baseSalary;
    }

    public User(String id, ProperName firstName, ProperName lastName, Email email, BaseSalary baseSalary){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.baseSalary = baseSalary;
    }

    public User(ProperName firstName, ProperName lastName, Email email, BaseSalary baseSalary){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.baseSalary = baseSalary;
    }

    public String getFullName() {
        return firstName.getValue() + " " + lastName.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                ", name=" + getFullName() +
                ", email=" + email +
                '}';
    }

}
