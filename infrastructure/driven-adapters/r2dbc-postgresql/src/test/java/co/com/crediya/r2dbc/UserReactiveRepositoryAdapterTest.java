package co.com.crediya.r2dbc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;

import co.com.crediya.model.user.User;
import co.com.crediya.model.valueobject.BaseSalary;
import co.com.crediya.model.valueobject.Email;
import co.com.crediya.model.valueobject.ProperName;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.UserDomainMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @InjectMocks
    UserReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Mock 
    UserDomainMapper domainMapper;

    private User testUser;
    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        // Create test domain user
        testUser = User.builder()
                .id("test-id-123")
                .firstName(new ProperName("John", "first name"))
                .lastName(new ProperName("Doe", "last name"))
                .email(new Email("john.doe@test.com"))
                .baseSalary(new BaseSalary(new BigDecimal("75000")))
                .birthDate(LocalDate.of(1990, 5, 15))
                .address("123 Test Street")
                .phone("555-0123")
                .build();

        // Create test entity
        testUserEntity = new UserEntity();
        testUserEntity.setId("test-id-123");
        testUserEntity.setFirstName("John");
        testUserEntity.setLastName("Doe");
        testUserEntity.setEmail("john.doe@test.com");
        testUserEntity.setBaseSalary(new BigDecimal("75000"));
        testUserEntity.setBirthDate(LocalDate.of(1990, 5, 15));
        testUserEntity.setAddress("123 Test Street");
        testUserEntity.setPhone("555-0123");
        testUserEntity.setCreatedAt(LocalDateTime.now());
        testUserEntity.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldFindUserById() {
        // Given
        String userId = "test-id-123";
        when(repository.findById(userId)).thenReturn(Mono.just(testUserEntity));
        when(domainMapper.entityToDomain(testUserEntity)).thenReturn(testUser);

        // When
        Mono<User> result = repositoryAdapter.findById(userId);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> 
                    user.getId().equals("test-id-123") &&
                    user.getEmail().getValue().equals("john.doe@test.com") &&
                    user.getFirstName().getValue().equals("John")
                )
                .verifyComplete();

        verify(repository, times(1)).findById(userId);
        verify(domainMapper, times(1)).entityToDomain(testUserEntity);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // Given
        String userId = "non-existent-id";
        when(repository.findById(userId)).thenReturn(Mono.empty());

        // When
        Mono<User> result = repositoryAdapter.findById(userId);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(repository, times(1)).findById(userId);
    }

    @Test
    void shouldFindAllUsers() {
        // Given
        UserEntity secondUserEntity = new UserEntity();
        secondUserEntity.setId("test-id-456");
        secondUserEntity.setFirstName("Jane");
        secondUserEntity.setLastName("Smith");
        secondUserEntity.setEmail("jane.smith@test.com");
        secondUserEntity.setBaseSalary(new BigDecimal("80000"));

        User secondUser = User.builder()
                .id("test-id-456")
                .firstName(new ProperName("Jane", "first name"))
                .lastName(new ProperName("Smith", "last name"))
                .email(new Email("jane.smith@test.com"))
                .baseSalary(new BaseSalary(new BigDecimal("80000")))
                .build();

        when(repository.findAll()).thenReturn(Flux.just(testUserEntity, secondUserEntity));
        when(domainMapper.entityToDomain(testUserEntity)).thenReturn(testUser);
        when(domainMapper.entityToDomain(secondUserEntity)).thenReturn(secondUser);

        // When
        Flux<User> result = repositoryAdapter.findAll();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getId().equals("test-id-123"))
                .expectNextMatches(user -> user.getId().equals("test-id-456"))
                .verifyComplete();

        verify(repository, times(1)).findAll();
        verify(domainMapper, times(2)).entityToDomain(any(UserEntity.class));
    }

    @Test
    void shouldSaveUser() {
        // Given
        when(domainMapper.domainToEntity(testUser)).thenReturn(testUserEntity);
        when(repository.save(testUserEntity)).thenReturn(Mono.just(testUserEntity));
        when(domainMapper.entityToDomain(testUserEntity)).thenReturn(testUser);

        // When
        Mono<User> result = repositoryAdapter.save(testUser);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> 
                    user.getId().equals(testUser.getId()) &&
                    user.getEmail().getValue().equals(testUser.getEmail().getValue())
                )
                .verifyComplete();

        verify(domainMapper, times(1)).domainToEntity(testUser);
        verify(repository, times(1)).save(testUserEntity);
        verify(domainMapper, times(1)).entityToDomain(testUserEntity);
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        Email email = new Email("john.doe@test.com");
        when(repository.findByEmail(email.getValue())).thenReturn(Mono.just(testUserEntity));
        when(domainMapper.entityToDomain(testUserEntity)).thenReturn(testUser);

        // When
        Mono<User> result = repositoryAdapter.findByEmail(email);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> 
                    user.getEmail().getValue().equals("john.doe@test.com")
                )
                .verifyComplete();

        verify(repository, times(1)).findByEmail("john.doe@test.com");
        verify(domainMapper, times(1)).entityToDomain(testUserEntity);
    }

    @Test
    void shouldReturnTrueWhenUserExistsByEmail() {
        // Given
        Email email = new Email("john.doe@test.com");
        when(repository.existsByEmail(email.getValue())).thenReturn(Mono.just(true));

        // When
        Mono<Boolean> result = repositoryAdapter.existsByEmail(email);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repository, times(1)).existsByEmail("john.doe@test.com");
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExistByEmail() {
        // Given
        Email email = new Email("nonexistent@test.com");
        when(repository.existsByEmail(email.getValue())).thenReturn(Mono.just(false));

        // When
        Mono<Boolean> result = repositoryAdapter.existsByEmail(email);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(repository, times(1)).existsByEmail("nonexistent@test.com");
    }

    @Test
    void shouldDeleteUserById() {
        // Given
        String userId = "test-id-123";
        when(repository.deleteById(userId)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = repositoryAdapter.deleteById(userId);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(repository, times(1)).deleteById(userId);
    }

    @Test
    void shouldCountUsers() {
        // Given
        when(repository.count()).thenReturn(Mono.just(5L));

        // When
        Mono<Long> result = repositoryAdapter.count();

        // Then
        StepVerifier.create(result)
                .expectNext(5L)
                .verifyComplete();

        verify(repository, times(1)).count();
    }

    @Test
    void shouldHandleErrorWhenSavingUser() {
        // Given
        RuntimeException exception = new RuntimeException("Database error");
        when(domainMapper.domainToEntity(testUser)).thenReturn(testUserEntity);
        when(repository.save(testUserEntity)).thenReturn(Mono.error(exception));

        // When
        Mono<User> result = repositoryAdapter.save(testUser);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(error -> 
                    error instanceof RuntimeException &&
                    error.getMessage().contains("Failed to save user with id: test-id-123")
                )
                .verify();

        verify(domainMapper, times(1)).domainToEntity(testUser);
        verify(repository, times(1)).save(testUserEntity);
    }

    @Test
    void shouldHandleErrorWhenFindingByEmail() {
        // Given
        Email email = new Email("john.doe@test.com");
        RuntimeException exception = new RuntimeException("Database connection failed");
        when(repository.findByEmail(email.getValue())).thenReturn(Mono.error(exception));

        // When
        Mono<User> result = repositoryAdapter.findByEmail(email);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(error -> 
                    error instanceof RuntimeException &&
                    error.getMessage().contains("Failed to find user by email: john.doe@test.com")
                )
                .verify();

        verify(repository, times(1)).findByEmail("john.doe@test.com");
    }

    @Test
    void shouldHandleInvalidDataFromDatabase() {
        // Given - Entity with invalid data that will fail value object construction
        UserEntity invalidEntity = new UserEntity();
        invalidEntity.setId("invalid-user");
        invalidEntity.setFirstName("Jo"); // Too short - will fail ProperName validation
        invalidEntity.setLastName("Doe");
        invalidEntity.setEmail("john.doe@test.com");
        invalidEntity.setBaseSalary(new BigDecimal("75000"));

        when(repository.findById("invalid-user")).thenReturn(Mono.just(invalidEntity));
        when(domainMapper.entityToDomain(invalidEntity))
            .thenThrow(new IllegalStateException("Invalid data in database for user ID: invalid-user"));

        // When
        Mono<User> result = repositoryAdapter.findById("invalid-user");

        // Then
        StepVerifier.create(result)
                .expectError(IllegalStateException.class)
                .verify();

        verify(repository, times(1)).findById("invalid-user");
        verify(domainMapper, times(1)).entityToDomain(invalidEntity);
    }
}
