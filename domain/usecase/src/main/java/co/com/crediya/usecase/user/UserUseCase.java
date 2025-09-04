package co.com.crediya.usecase.user;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.PasswordEncoderService;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.exception.UserAlreadyExistsException;
import co.com.crediya.model.user.exception.UserNotFoundException;
import co.com.crediya.model.user.exception.InvalidUserDataException;
import co.com.crediya.model.user.exception.AuthenticationException;
import co.com.crediya.model.valueobject.Email;
import co.com.crediya.model.valueobject.Password;
import lombok.RequiredArgsConstructor;
import co.com.crediya.model.valueobject.DocumentId;
import co.com.crediya.model.user.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoder;

    /**
     * Creates a new user with authentication fields
     * @param user User to be created
     * @return Mono with the created user
     * @throws UserAlreadyExistsException if user with email already exists
     * @throws UserAlreadyExistsException if user with document ID already exists
     */
    public Mono<User> createUser(User user) {
        if (user == null) {
            return Mono.error(new InvalidUserDataException("User cannot be null"));
        }
        
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new UserAlreadyExistsException("User with email already exists: " + user.getEmail().getValue()));
                    }
                    
                    if (user.getDocumentId() != null) {
                        return userRepository.existsByDocumentId(user.getDocumentId());
                    }
                    return Mono.just(false);
                })
                .flatMap(documentExists -> {
                    if (documentExists) {
                        return Mono.error(new UserAlreadyExistsException("User with document ID already exists: " + user.getDocumentId().getMaskedValue()));
                    }
                    
                    User userWithHashedPassword = hashPasswordForUser(user);
                    return userRepository.save(userWithHashedPassword);
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
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with ID: " + id)));
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
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with email: " + email.getValue())));
    }

    /**
     * Finds a user by their document ID
     * @param documentId Document ID to search
     * @return Mono with the found user
     * @throws UserNotFoundException if user is not found
     */
    public Mono<User> findUserByDocumentId(DocumentId documentId) {
        if (documentId == null) {
            return Mono.error(new InvalidUserDataException("Document ID cannot be null"));
        }
        
        return userRepository.findByDocumentId(documentId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with document ID: " + documentId.getMaskedValue())));
    }

    /**
     * Finds users by their role
     * @param roleCode Role code to search
     * @return Flux with users having the specified role
     */
    public Flux<User> findUsersByRole(String roleCode) {
        if (roleCode == null || roleCode.trim().isEmpty()) {
            return Flux.error(new InvalidUserDataException("Role code cannot be null or empty"));
        }
        
        try {
            Role role = Role.fromCode(roleCode);
            return userRepository.findByRole(role);
        } catch (IllegalArgumentException e) {
            return Flux.error(new InvalidUserDataException("Invalid role code: " + roleCode));
        }
    }

    /**
     * Finds all users in the system
     * @return Flux with all users
     */
    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Authenticates a user with email and password
     * @param email User email
     * @param password Plain text password
     * @return Mono with the authenticated user
     * @throws AuthenticationException if authentication fails
     */
    public Mono<User> authenticateUser(Email email, String password) {
        if (email == null) {
            return Mono.error(new InvalidUserDataException("Email cannot be null"));
        }
        
        if (password == null || password.trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("Password cannot be null or empty"));
        }
        
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new AuthenticationException("Invalid email or password")))
                .flatMap(user -> {
                    if (user.getPassword() == null) {
                        return Mono.error(new AuthenticationException("User has no password set"));
                    }
                    
                    // Verify password
                    boolean passwordMatches = passwordEncoder.matches(password, user.getPassword().getValue());
                    
                    if (passwordMatches) {
                        return Mono.just(user);
                    } else {
                        return Mono.error(new AuthenticationException("Invalid email or password"));
                    }
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
     * Checks if a user exists by document ID
     * @param documentId Document ID to verify
     * @return Mono with true if exists, false otherwise
     * @throws InvalidUserDataException if document ID is null
     */
    public Mono<Boolean> userExistsByDocumentId(DocumentId documentId) {
        if (documentId == null) {
            return Mono.error(new InvalidUserDataException("Document ID cannot be null"));
        }
        
        return userRepository.existsByDocumentId(documentId);
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

    /**
     * Finds users with elevated privileges (ADMIN or MANAGER roles)
     * @return Flux with users having elevated privileges
     */
    public Flux<User> findUsersWithElevatedPrivileges() {
        return userRepository.findUsersWithElevatedPrivileges();
    }

    /**
     * Counts users by role
     * @param roleCode Role code to count
     * @return Mono with the count
     */
    public Mono<Long> countUsersByRole(String roleCode) {
        if (roleCode == null || roleCode.trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("Role code cannot be null or empty"));
        }
        
        try {
            Role role = Role.fromCode(roleCode);
            return userRepository.countUsersByRole(role);
        } catch (IllegalArgumentException e) {
            return Mono.error(new InvalidUserDataException("Invalid role code: " + roleCode));
        }
    }

    /**
     * Helper method to hash password for a user
     * @param user User with plain text password
     * @return User with hashed password
     */
    private User hashPasswordForUser(User user) {
        if (user.getPassword() == null || user.getPassword().isHashed()) {
            return user;
        }
        
        String hashedPassword = passwordEncoder.encode(user.getPassword().getValue());
        
        return user.toBuilder()
                .password(Password.fromHash(hashedPassword))
                .build();
    }
}