package co.com.crediya.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;

import co.com.crediya.api.dto.ApiResponse;
import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.api.mapper.UserMapper;
import co.com.crediya.model.user.exception.InvalidUserDataException;
import co.com.crediya.model.user.exception.UserAlreadyExistsException;
import co.com.crediya.model.user.exception.UserNotFoundException;
import co.com.crediya.model.valueobject.Email;
import co.com.crediya.model.valueobject.exception.ValueObjectException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Operations for managing users")
public class Handler {
    
    private final UserUseCase userUseCase;

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(UserDTO.class)
                .doOnNext(dto -> log.debug("Creating user: {}", dto.email()))
                .flatMap(this::validateCreateRequest)
                .map(UserMapper::toUser)
                .flatMap(userUseCase::createUser)
                .map(UserMapper::toDTO)
                .map(userDto -> ApiResponse.success(userDto, "User created successfully"))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(throwable -> handleError(throwable, request.path()));
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return userUseCase.findAllUsers()
                .doOnNext(user -> log.debug("Found user: {}", user.getId()))
                .map(UserMapper::toDTO)
                .collectList()
                .map(users -> ApiResponse.success(users, "Users retrieved successfully"))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(throwable -> handleError(throwable, request.path()));
    }

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        String userId = request.pathVariable("id");
        log.debug("Getting user by ID: {}", userId);
        
        return userUseCase.findUserById(userId)
                .map(UserMapper::toDTO)
                .map(userDto -> ApiResponse.success(userDto, "User found successfully"))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(throwable -> handleError(throwable, request.path()));
    }

    public Mono<ServerResponse> getUserByEmail(ServerRequest request) {
        return request.queryParam("email")
                .map(emailValue -> {
                    log.debug("Getting user by email: {}", emailValue);
                    try {
                        Email email = new Email(emailValue);
                        return userUseCase.findUserByEmail(email)
                                .map(UserMapper::toDTO)
                                .map(userDto -> ApiResponse.success(userDto, "User found successfully"))
                                .flatMap(response -> ServerResponse.ok().bodyValue(response));
                    } catch (ValueObjectException e) {
                        log.warn("Invalid email format: {}", emailValue);
                        return ServerResponse.badRequest()
                                .bodyValue(ApiResponse.error("Invalid email format: " + emailValue));
                    }
                })
                .orElse(ServerResponse.badRequest()
                        .bodyValue(ApiResponse.error("Email parameter is required")))
                .onErrorResume(throwable -> handleError(throwable, request.path()));
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        String userId = request.pathVariable("id");
        log.debug("Updating user with ID: {}", userId);
        
        return request.bodyToMono(UserDTO.class)
                .flatMap(dto -> validateUpdateRequest(dto, userId))
                .map(dto -> new UserDTO(userId, dto.firstName(), dto.lastName(), 
                        dto.birthDate(), dto.address(), dto.phone(), dto.email(), dto.baseSalary()))
                .map(UserMapper::toUser)
                .flatMap(user -> userUseCase.updateUser(userId, user))
                .map(UserMapper::toDTO)
                .map(userDto -> ApiResponse.success(userDto, "User updated successfully"))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(throwable -> handleError(throwable, request.path()));
    }

    public Mono<ServerResponse> checkUserExists(ServerRequest request) {
        return request.queryParam("email")
                .map(emailValue -> {
                    log.debug("Checking if user exists with email: {}", emailValue);
                    try {
                        Email email = new Email(emailValue);
                        return userUseCase.userExistsByEmail(email)
                                .map(exists -> ApiResponse.success(exists, 
                                    exists ? "User exists" : "User does not exist"))
                                .flatMap(response -> ServerResponse.ok().bodyValue(response));
                    } catch (ValueObjectException e) {
                        log.warn("Invalid email format: {}", emailValue);
                        return ServerResponse.badRequest()
                                .bodyValue(ApiResponse.error("Invalid email format: " + emailValue));
                    }
                })
                .orElse(ServerResponse.badRequest()
                        .bodyValue(ApiResponse.error("Email parameter is required")))
                .onErrorResume(throwable -> handleError(throwable, request.path()));
    }

    public Mono<ServerResponse> checkUserExistsById(ServerRequest request) {
        return Mono.fromCallable(() -> request.pathVariable("id"))
                .flatMap(userId -> {
                    log.debug("Checking if user exists with ID: {}", userId);
                    
                    // Validar que el ID no esté vacío
                    if (userId == null || userId.trim().isEmpty()) {
                        return ServerResponse.badRequest()
                                .bodyValue(ApiResponse.error("User ID cannot be empty"));
                    }
                    
                    return userUseCase.userExistsById(userId)
                            .map(exists -> ApiResponse.success(exists, 
                                exists ? "User exists" : "User does not exist"))
                            .flatMap(response -> ServerResponse.ok().bodyValue(response));
                })
                .onErrorResume(throwable -> handleError(throwable, request.path()));
    }

    public Mono<ServerResponse> checkUserExistsByIdQuery(ServerRequest request) {
        return request.queryParam("id")
                .map(userId -> {
                    log.debug("Checking if user exists with ID: {}", userId);
                    
                    // Validar que el ID no esté vacío
                    if (userId.trim().isEmpty()) {
                        return ServerResponse.badRequest()
                                .bodyValue(ApiResponse.error("User ID cannot be empty"));
                    }
                    
                    return userUseCase.userExistsById(userId)
                            .map(exists -> ApiResponse.success(exists, 
                                exists ? "User exists" : "User does not exist"))
                            .flatMap(response -> ServerResponse.ok().bodyValue(response));
                })
                .orElse(ServerResponse.badRequest()
                        .bodyValue(ApiResponse.error("ID parameter is required")))
                .onErrorResume(throwable -> handleError(throwable, request.path()));
    }

    private Mono<UserDTO> validateCreateRequest(UserDTO dto) {
        if (dto.firstName() == null || dto.firstName().trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("First name is required"));
        }
        if (dto.lastName() == null || dto.lastName().trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("Last name is required"));
        }
        if (dto.email() == null || dto.email().trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("Email is required"));
        }
        if (dto.baseSalary() == null) {
            return Mono.error(new InvalidUserDataException("Base salary is required"));
        }
        return Mono.just(dto);
    }

    private Mono<UserDTO> validateUpdateRequest(UserDTO dto, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("User ID is required"));
        }
        return validateCreateRequest(dto);
    }

    private Mono<ServerResponse> handleError(Throwable throwable, String path) {
        log.error("Error processing request at path {}: {}", path, throwable.getMessage(), throwable);
        
        if (throwable instanceof UserNotFoundException) {
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .bodyValue(createErrorResponse(throwable.getMessage(), "USER_NOT_FOUND", path));
        }
        
        if (throwable instanceof UserAlreadyExistsException) {
            return ServerResponse.status(HttpStatus.CONFLICT)
                    .bodyValue(createErrorResponse(throwable.getMessage(), "USER_ALREADY_EXISTS", path));
        }
        
        if (throwable instanceof InvalidUserDataException || throwable instanceof ValueObjectException) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .bodyValue(createErrorResponse(throwable.getMessage(), "INVALID_DATA", path));
        }
        
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(createErrorResponse("Internal server error", "INTERNAL_ERROR", path));
    }

    private ErrorResponse createErrorResponse(String message, String errorCode, String path) {
        return new ErrorResponse(message, errorCode, LocalDateTime.now(), path);
    }
}