package co.com.crediya.model.user.gateways;

import java.util.List;

import co.com.crediya.model.user.User;

public interface UserRepository {

    void saveUser(User user);

    List<User> getAllUsers();

    User getUserById(String id);

    User getUserByEmail(String Email);

    void deleteUSer(User user);
    
}
