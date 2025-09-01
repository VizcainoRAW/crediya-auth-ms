package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;
import co.com.crediya.model.valueobject.Email;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface UserRepository {
    
    /**
     * Saves a new user into the repository
     * @param user User to be saved
     * @return Mono with the saved user
     */
    Mono<User> save(User user);

    Flux<User> findAll();

    /**
     * Finds a user by their email address
     * @param email Email address to search
     * @return Mono with the found user or empty if not found
     */
    Mono<User> findByEmail(Email email);
    
    /**
     * Finds a user by their ID
     * @param id User ID
     * @return Mono with the found user or empty if not found
     */
    Mono<User> findById(String id);
    
    /**
     * Checks if a user with the given email already exists
     * @param email Email address to verify
     * @return Mono with true if exists, false otherwise
     */
    Mono<Boolean> existsByEmail(Email email);
}
