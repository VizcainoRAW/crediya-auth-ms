package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.User;
import co.com.crediya.model.valueobject.DocumentId;
import co.com.crediya.model.valueobject.Email;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface UserRepository {
    
    
    /**
     * Saves a user
     * @param user User to save
     * @return Mono with the saved user
     */
    Mono<User> save(User user);
    
    /**
     * Finds a user by ID
     * @param id User ID
     * @return Mono with the user or empty if not found
     */
    Mono<User> findById(String id);
    
    /**
     * Finds a user by email
     * @param email Email to search
     * @return Mono with the user or empty if not found
     */
    Mono<User> findByEmail(Email email);
    
    /**
     * Finds a user by document ID
     * @param documentId Document ID to search
     * @return Mono with the user or empty if not found
     */
    Mono<User> findByDocumentId(DocumentId documentId);
    
    /**
     * Finds users by role
     * @param role Role to search
     * @return Flux with users having the specified role
     */
    Flux<User> findByRole(Role role);
    
    /**
     * Finds all users
     * @return Flux with all users
     */
    Flux<User> findAll();
    
    /**
     * Checks if a user exists by email
     * @param email Email to check
     * @return Mono with true if exists, false otherwise
     */
    Mono<Boolean> existsByEmail(Email email);
    
    /**
     * Checks if a user exists by document ID
     * @param documentId Document ID to check
     * @return Mono with true if exists, false otherwise
     */
    Mono<Boolean> existsByDocumentId(DocumentId documentId);
    
    /**
     * Finds users with elevated privileges (ADMIN or MANAGER roles)
     * @return Flux with users having elevated privileges
     */
    Flux<User> findUsersWithElevatedPrivileges();
    
    /**
     * Counts users by role
     * @param role Role to count
     * @return Mono with the count
     */
    Mono<Long> countUsersByRole(Role role);
}
