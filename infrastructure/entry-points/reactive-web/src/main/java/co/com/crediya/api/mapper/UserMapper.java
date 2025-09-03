package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.model.user.User;
import co.com.crediya.model.valueobject.BaseSalary;
import co.com.crediya.model.valueobject.Email;
import co.com.crediya.model.valueobject.ProperName;

public class UserMapper {

    public static User toUser(UserDTO dto){
        return new User(
            dto.id(),
            new ProperName(dto.firstName(), "first name"),
            new ProperName(dto.lastName(), "last name"),
            dto.birthDate(),
            dto.address(),
            dto.phone(),
            new Email(dto.email()),         
            new BaseSalary(dto.baseSalary())
        );
    }

    public static User toUser(UserRequestDTO dto){
        return new User(
            new ProperName(dto.firstName(), "first name"),
            new ProperName(dto.lastName(), "last name"),
            dto.birthDate(),
            dto.address(),
            dto.phone(),
            new Email(dto.email()),         
            new BaseSalary(dto.baseSalary())
        );
    }

    public static UserDTO toDTO(User user){
        return new UserDTO(
            user.getId(),
            user.getFirstName().getValue(),
            user.getLastName().getValue(),
            user.getBirthDate(),
            user.getAddress(),
            user.getPhone(),
            user.getEmail().getValue(),
            user.getBaseSalary().getValue()
        );
    }

}