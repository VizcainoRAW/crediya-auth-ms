package co.com.crediya.r2dbc;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.crediya.r2dbc.entity.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, UUID>, ReactiveQueryByExampleExecutor<UserEntity> {

    /**
     * Find user by email
     * @param email Email to search for
     * @return Mono containing the user entity if found
     */
    Mono<UserEntity> findByEmail(String email);
    
    /**
     * Check if a user exists with the given email
     * @param email Email to check
     * @return Mono containing true if exists, false otherwise
     */
    Mono<Boolean> existsByEmail(String email);
    
    /**
     * Find user by document ID
     * @param documentId Document ID to search for
     * @return Mono containing the user entity if found
     */
    Mono<UserEntity> findByDocumentId(String documentId);
    
    /**
     * Check if a user exists with the given document ID
     * @param documentId Document ID to check
     * @return Mono containing true if exists, false otherwise
     */
    Mono<Boolean> existsByDocumentId(String documentId);
    
    /**
     * Find users by role
     * @param role Role to search for
     * @return Flux containing all users with the specified role
     */
    Flux<UserEntity> findByRole(String role);
    
    /**
     * Find users by document type
     * @param documentType Document type to search for
     * @return Flux containing all users with the specified document type
     */
    Flux<UserEntity> findByDocumentType(String documentType);
    
    /**
     * Find user by email using custom query (alternative approach)
     * @param email Email to search for
     * @return Mono containing the user entity if found
     */
    @Query("SELECT * FROM users WHERE email = :email")
    Mono<UserEntity> findUserByEmailCustom(String email);
    
    /**
     * Find user by document type and document ID
     * @param documentType Document type
     * @param documentId Document ID
     * @return Mono containing the user entity if found
     */
    @Query("SELECT * FROM users WHERE document_type = :documentType AND document_id = :documentId")
    Mono<UserEntity> findByDocumentTypeAndDocumentId(String documentType, String documentId);
    
    /**
     * Count users with salary in range (example of custom query)
     * @param minSalary Minimum salary
     * @param maxSalary Maximum salary
     * @return Mono containing count
     */
    @Query("SELECT COUNT(*) FROM users WHERE base_salary BETWEEN :minSalary AND :maxSalary")
    Mono<Long> countUsersBySalaryRange(java.math.BigDecimal minSalary, java.math.BigDecimal maxSalary);
    
    /**
     * Count users by role
     * @param role Role to count
     * @return Mono containing count
     */
    @Query("SELECT COUNT(*) FROM users WHERE role = :role")
    Mono<Long> countUsersByRole(String role);
    
    /**
     * Find users with elevated privileges (ADMIN or MANAGER roles)
     * @return Flux containing users with elevated privileges
     */
    @Query("SELECT * FROM users WHERE role IN ('ADMIN', 'MANAGER')")
    Flux<UserEntity> findUsersWithElevatedPrivileges();
    
    /**
     * Check if email exists excluding a specific user ID (useful for updates)
     * @param email Email to check
     * @param userId User ID to exclude
     * @return Mono containing true if exists, false otherwise
     */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE email = :email AND id != :userId")
    Mono<Boolean> existsByEmailAndIdNot(String email, UUID userId);
    
    /**
     * Check if document ID exists excluding a specific user ID (useful for updates)
     * @param documentId Document ID to check
     * @param userId User ID to exclude
     * @return Mono containing true if exists, false otherwise
     */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE document_id = :documentId AND id != :userId")
    Mono<Boolean> existsByDocumentIdAndIdNot(String documentId, UUID userId);
}
