package co.com.crediya.r2dbc;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.crediya.r2dbc.entity.UserEntity;
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
     * Find user by email using custom query (alternative approach)
     * @param email Email to search for
     * @return Mono containing the user entity if found
     */
    @Query("SELECT * FROM users WHERE email = :email")
    Mono<UserEntity> findUserByEmailCustom(String email);
    
    /**
     * Count users with salary in range (example of custom query)
     * @param minSalary Minimum salary
     * @param maxSalary Maximum salary
     * @return Mono containing count
     */
    @Query("SELECT COUNT(*) FROM users WHERE base_salary BETWEEN :minSalary AND :maxSalary")
    Mono<Long> countUsersBySalaryRange(java.math.BigDecimal minSalary, java.math.BigDecimal maxSalary);
}
