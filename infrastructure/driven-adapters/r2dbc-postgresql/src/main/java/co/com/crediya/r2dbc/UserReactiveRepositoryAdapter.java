package co.com.crediya.r2dbc;

import co.com.crediya.model.user.DocumentType;
import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.valueobject.DocumentId;
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

    // New authentication-related methods
    public Mono<User> findByDocumentId(DocumentId documentId) {
        logger.debug("Finding user by document ID: {}", documentId.getMaskedValue());
        
        return repository.findByDocumentId(documentId.getValue())
                .map(domainMapper::entityToDomain)
                .doOnSuccess(user -> logger.debug("User found with document ID: {}", documentId.getMaskedValue()))
                .switchIfEmpty(Mono.fromRunnable(() ->
                        logger.debug("No user found with document ID: {}", documentId.getMaskedValue())))
                .doOnError(error -> logger.error("Error finding user by document ID: {}", documentId.getMaskedValue(), error))
                .onErrorMap(Exception.class, ex ->
                        new RuntimeException("Failed to find user by document ID: " + documentId.getMaskedValue(), ex));
    }

    public Mono<Boolean> existsByDocumentId(DocumentId documentId) {
        logger.debug("Checking if user exists by document ID: {}", documentId.getMaskedValue());
        
        return repository.existsByDocumentId(documentId.getValue())
                .doOnSuccess(exists -> logger.debug("User exists check for document ID {}: {}", documentId.getMaskedValue(), exists))
                .doOnError(error -> logger.error("Error checking user existence by document ID: {}", documentId.getMaskedValue(), error))
                .onErrorMap(Exception.class, ex -> 
                    new RuntimeException("Failed to check user existence by document ID: " + documentId.getMaskedValue(), ex));
    }

    public Flux<User> findByRole(Role role) {
        logger.debug("Finding users by role: {}", role.getCode());
        
        return repository.findByRole(role.getCode())
                .map(domainMapper::entityToDomain)
                .doOnComplete(() -> logger.debug("Completed finding users by role: {}", role.getCode()))
                .doOnError(error -> logger.error("Error finding users by role: {}", role.getCode(), error))
                .onErrorMap(Exception.class, ex -> 
                    new RuntimeException("Failed to find users by role: " + role.getCode(), ex));
    }

    public Flux<User> findByDocumentType(DocumentType documentType) {
        logger.debug("Finding users by document type: {}", documentType.getCode());
        
        return repository.findByDocumentType(documentType.getCode())
                .map(domainMapper::entityToDomain)
                .doOnComplete(() -> logger.debug("Completed finding users by document type: {}", documentType.getCode()))
                .doOnError(error -> logger.error("Error finding users by document type: {}", documentType.getCode(), error))
                .onErrorMap(Exception.class, ex -> 
                    new RuntimeException("Failed to find users by document type: " + documentType.getCode(), ex));
    }

    public Mono<User> findByDocumentTypeAndDocumentId(DocumentType documentType, DocumentId documentId) {
        logger.debug("Finding user by document type: {} and document ID: {}", 
                documentType.getCode(), documentId.getMaskedValue());
        
        return repository.findByDocumentTypeAndDocumentId(documentType.getCode(), documentId.getValue())
                .map(domainMapper::entityToDomain)
                .doOnSuccess(user -> logger.debug("User found with document type: {} and document ID: {}", 
                        documentType.getCode(), documentId.getMaskedValue()))
                .switchIfEmpty(Mono.fromRunnable(() ->
                        logger.debug("No user found with document type: {} and document ID: {}", 
                                documentType.getCode(), documentId.getMaskedValue())))
                .doOnError(error -> logger.error("Error finding user by document type: {} and document ID: {}", 
                        documentType.getCode(), documentId.getMaskedValue(), error))
                .onErrorMap(Exception.class, ex ->
                        new RuntimeException("Failed to find user by document type and document ID", ex));
    }

    public Flux<User> findUsersWithElevatedPrivileges() {
        logger.debug("Finding users with elevated privileges");
        
        return repository.findUsersWithElevatedPrivileges()
                .map(domainMapper::entityToDomain)
                .doOnComplete(() -> logger.debug("Completed finding users with elevated privileges"))
                .doOnError(error -> logger.error("Error finding users with elevated privileges", error))
                .onErrorMap(Exception.class, ex -> 
                    new RuntimeException("Failed to find users with elevated privileges", ex));
    }

    public Mono<Long> countUsersByRole(Role role) {
        logger.debug("Counting users by role: {}", role.getCode());
        
        return repository.countUsersByRole(role.getCode())
                .doOnSuccess(count -> logger.debug("Total users count for role {}: {}", role.getCode(), count))
                .doOnError(error -> logger.error("Error counting users by role: {}", role.getCode(), error))
                .onErrorMap(Exception.class, ex -> 
                    new RuntimeException("Failed to count users by role: " + role.getCode(), ex));
    }

    // Validation methods for updates
    public Mono<Boolean> existsByEmailAndIdNot(Email email, String userId) {
        logger.debug("Checking if email exists excluding user ID: {}", userId);
        
        return repository.existsByEmailAndIdNot(email.getValue(), UUID.fromString(userId))
                .doOnSuccess(exists -> logger.debug("Email exists check (excluding user {}): {}", userId, exists))
                .doOnError(error -> logger.error("Error checking email existence excluding user: {}", userId, error))
                .onErrorMap(Exception.class, ex -> 
                    new RuntimeException("Failed to check email existence excluding user: " + userId, ex));
    }

    public Mono<Boolean> existsByDocumentIdAndIdNot(DocumentId documentId, String userId) {
        logger.debug("Checking if document ID exists excluding user ID: {}", userId);
        
        return repository.existsByDocumentIdAndIdNot(documentId.getValue(), UUID.fromString(userId))
                .doOnSuccess(exists -> logger.debug("Document ID exists check (excluding user {}): {}", userId, exists))
                .doOnError(error -> logger.error("Error checking document ID existence excluding user: {}", userId, error))
                .onErrorMap(Exception.class, ex -> 
                    new RuntimeException("Failed to check document ID existence excluding user: " + userId, ex));
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

    public Mono<User> findByDocumentId(String id) {
        return findByDocumentId(new DocumentId(id));
    }
}
