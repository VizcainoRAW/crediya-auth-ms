package co.com.crediya.usecase.user;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
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
     * @throws IllegalArgumentException if user with email already exists
     */
    public Mono<User> createUser(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException(
                            "User with email " + user.getEmail().getValue() + " already exists"));
                    }
                    return userRepository.save(user);
                });
    }

    public Mono<User> findUserById(String id) {
        return userRepository.findById(id);
    }

    public Mono<User> findUserByEmail(Email email) {
        return userRepository.findByEmail(email);
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Updates an existing user
     * @param userId ID of the user to update
     * @param updatedUser User data to update
     * @return Mono with the updated user
     * @throws IllegalArgumentException if user not found or email conflict
     */
    public Mono<User> updateUser(String userId, User updatedUser) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found with ID: " + userId)))
                .flatMap(existingUser -> {
                    // Check if email is being changed and if new email already exists
                    if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                        return userRepository.existsByEmail(updatedUser.getEmail())
                                .flatMap(emailExists -> {
                                    if (emailExists) {
                                        return Mono.error(new IllegalArgumentException(
                                            "Email " + updatedUser.getEmail().getValue() + " is already in use"));
                                    }
                                    return saveUpdatedUser(userId, updatedUser);
                                });
                    }
                    return saveUpdatedUser(userId, updatedUser);
                });
    }

    public Mono<Boolean> userExistsByEmail(Email email) {
        return userRepository.existsByEmail(email);
    }

    public Mono<Boolean> userExistsById(String id) {
        return userRepository.findById(id)
                .hasElement();
    }

    private Mono<User> saveUpdatedUser(String userId, User updatedUser) {
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
    }
}