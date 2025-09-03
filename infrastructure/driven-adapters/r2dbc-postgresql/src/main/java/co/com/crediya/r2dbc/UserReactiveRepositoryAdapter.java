package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.valueobject.Email;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.helper.UserDomainMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;

import java.util.UUID;

import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User /* domain model */,
    UserEntity /* adapter model */,
    UUID,
    UserReactiveRepository
> implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserReactiveRepositoryAdapter.class);
    
    private final UserDomainMapper domainMapper;
    
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, 
                                        ObjectMapper mapper,
                                        UserDomainMapper domainMapper) {
        super(repository, mapper, entity -> domainMapper.entityToDomain((UserEntity) entity));
        this.domainMapper = domainMapper;
    }

    @Override
    protected UserEntity toData(User entity) {
        return domainMapper.domainToEntity(entity);
    }

    @Override
    @Transactional
    public Mono<User> save(User user) {
        logger.debug("Saving user with id: {}", user.getId());
        
        return super.save(user)
                .doOnSuccess(savedUser -> logger.info("User saved successfully with id: {}", savedUser.getId()))
                .doOnError(error -> logger.error("Error saving user with id: {}", user.getId(), error))
                .onErrorMap(Exception.class, ex -> 
                    new RuntimeException("Failed to save user with id: " + user.getId(), ex));
    }

    @Override
    public Flux<User> findAll() {
        logger.debug("Finding all users");
        
        return super.findAll()
                .doOnComplete(() -> logger.debug("Completed finding all users"))
                .doOnError(error -> logger.error("Error finding all users", error))
                .onErrorMap(Exception.class, ex -> 
                    new RuntimeException("Failed to find all users", ex));
    }

    @Override
    public Mono<User> findById(String id){
        return super.findById(UUID.fromString(id));
    }

    @Override
    public Mono<User> findByEmail(Email email) {
        logger.debug("Finding user by email: {}", email.getValue());
        
        return repository.findByEmail(email.getValue())
        .map(domainMapper::entityToDomain)
        .doOnSuccess(user -> logger.debug("User found with email: {}", email.getValue()))
        .switchIfEmpty(Mono.fromRunnable(() ->
                logger.debug("No user found with email: {}", email.getValue())))
        .doOnError(error -> logger.error("Error finding user by email: {}", email.getValue(), error))
        .onErrorMap(Exception.class, ex ->
                new RuntimeException("Failed to find user by email: " + email.getValue(), ex));

    }

    @Override
    public Mono<Boolean> existsByEmail(Email email) {
        logger.debug("Checking if user exists by email: {}", email.getValue());
        
        return repository.existsByEmail(email.getValue())
                .doOnSuccess(exists -> logger.debug("User exists check for email {}: {}", email.getValue(), exists))
                .doOnError(error -> logger.error("Error checking user existence by email: {}", email.getValue(), error))
                .onErrorMap(Exception.class, ex -> 
                    new RuntimeException("Failed to check user existence by email: " + email.getValue(), ex));
    }

    @Transactional
    public Mono<Void> deleteById(UUID id) {
        logger.debug("Deleting user with id: {}", id);
        
        return repository.deleteById(id)
                .doOnSuccess(result -> logger.info("User deleted successfully with id: {}", id))
                .doOnError(error -> logger.error("Error deleting user with id: {}", id, error))
                .onErrorMap(Exception.class, ex -> 
                    new RuntimeException("Failed to delete user with id: " + id, ex));
    }

    public Mono<Long> count() {
        logger.debug("Counting total users");
        
        return repository.count()
                .doOnSuccess(count -> logger.debug("Total users count: {}", count))
                .doOnError(error -> logger.error("Error counting users", error))
                .onErrorMap(Exception.class, ex -> 
                    new RuntimeException("Failed to count users", ex));
    }
}
