package co.com.crediya.usecase.user;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.exception.UserAlreadyExistsException;
import co.com.crediya.model.user.exception.UserNotFoundException;
import co.com.crediya.model.user.exception.InvalidUserDataException;
import co.com.crediya.model.valueobject.Email;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    
    private final UserRepository userRepository;

    /**
     * Creates a new user
     * @param user User to be created
     * @return Mono with the created user
     * @throws UserAlreadyExistsException if user with email already exists
     */
    public Mono<User> createUser(User user) {
        if (user == null) {
            return Mono.error(new InvalidUserDataException("User cannot be null"));
        }
        
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new UserAlreadyExistsException(user.getEmail().getValue()));
                    }
                    return userRepository.save(user);
                });
    }

    /**
     * Finds a user by their ID
     * @param id User ID
     * @return Mono with the found user
     * @throws UserNotFoundException if user is not found
     */
    public Mono<User> findUserById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("User ID cannot be null or empty"));
        }
        
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)));
    }

    /**
     * Finds a user by their email
     * @param email Email address to search
     * @return Mono with the found user
     * @throws UserNotFoundException if user is not found
     */
    public Mono<User> findUserByEmail(Email email) {
        if (email == null) {
            return Mono.error(new InvalidUserDataException("Email cannot be null"));
        }
        
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException("email", email.getValue())));
    }

    /**
     * Finds all users in the system
     * @return Flux with all users
     */
    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Updates an existing user
     * @param userId ID of the user to update
     * @param updatedUser User data to update
     * @return Mono with the updated user
     * @throws UserNotFoundException if user not found
     * @throws UserAlreadyExistsException if email conflict
     * @throws InvalidUserDataException if input data is invalid
     */
    public Mono<User> updateUser(String userId, User updatedUser) {
        if (userId == null || userId.trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("User ID cannot be null or empty"));
        }
        
        if (updatedUser == null) {
            return Mono.error(new InvalidUserDataException("Updated user data cannot be null"));
        }
        
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .flatMap(existingUser -> {
                    // Check if email is being changed and if new email already exists
                    if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                        return userRepository.existsByEmail(updatedUser.getEmail())
                                .flatMap(emailExists -> {
                                    if (emailExists) {
                                        return Mono.error(new UserAlreadyExistsException(
                                            updatedUser.getEmail().getValue()));
                                    }
                                    return saveUpdatedUser(userId, updatedUser);
                                });
                    }
                    return saveUpdatedUser(userId, updatedUser);
                });
    }

    /**
     * Deletes a user by ID
     * @param userId ID of the user to delete
     * @return Mono with the deleted user
     * @throws UserNotFoundException if user not found
     * @throws InvalidUserDataException if user ID is invalid
     */
    public Mono<User> deleteUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("User ID cannot be null or empty"));
        }
        
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .flatMap(user -> {
                    // Assuming you add a delete method to the repository
                    // return userRepository.deleteById(userId).thenReturn(user);
                    return Mono.just(user); // Placeholder for actual delete operation
                });
    }

    /**
     * Checks if a user exists by email
     * @param email Email address to verify
     * @return Mono with true if exists, false otherwise
     * @throws InvalidUserDataException if email is null
     */
    public Mono<Boolean> userExistsByEmail(Email email) {
        if (email == null) {
            return Mono.error(new InvalidUserDataException("Email cannot be null"));
        }
        
        return userRepository.existsByEmail(email);
    }

    /**
     * Checks if a user exists by ID
     * @param id User ID to verify
     * @return Mono with true if exists, false otherwise
     * @throws InvalidUserDataException if user ID is invalid
     */
    public Mono<Boolean> userExistsById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("User ID cannot be null or empty"));
        }
        
        return userRepository.findById(id)
                .hasElement();
    }

    private Mono<User> saveUpdatedUser(String userId, User updatedUser) {
        try {
            // Create updated user with the same ID
            User userToUpdate = User.builder()
                    .id(userId)
                    .firstName(updatedUser.getFirstName())
                    .lastName(updatedUser.getLastName())
                    .birthDate(updatedUser.getBirthDate())
                    .address(updatedUser.getAddress())
                    .phone(updatedUser.getPhone())
                    .email(updatedUser.getEmail())
                    .baseSalary(updatedUser.getBaseSalary())
                    .build();
                    
            return userRepository.save(userToUpdate);
        } catch (Exception e) {
            return Mono.error(new InvalidUserDataException("Failed to build updated user", e));
        }
    }
}