package co.com.crediya.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;

import co.com.crediya.api.dto.ApiResponse;
import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.api.dto.LoginRequestDTO;
import co.com.crediya.api.dto.LoginResponseDTO;
import co.com.crediya.api.dto.UserAuthDTO;
import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.api.mapper.UserMapper;
import co.com.crediya.model.user.exception.InvalidUserDataException;
import co.com.crediya.model.user.exception.UserAlreadyExistsException;
import co.com.crediya.model.user.exception.UserNotFoundException;
import co.com.crediya.model.valueobject.DocumentId;
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
    private final JwtService jwtService;

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(UserRequestDTO.class)
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

    public Mono<ServerResponse> getUserByDocumentId(ServerRequest request) {
        return request.queryParam("documentId")
                .<Mono<ServerResponse>>map(documentIdValue -> {
                    log.debug("Getting user by document ID: {}", documentIdValue);
                    try {
                        DocumentId documentId = new DocumentId(documentIdValue);
                        return userUseCase.findUserByDocumentId(documentId)
                                .map(UserMapper::toDTO)
                                .map(userDto -> ApiResponse.success(userDto, "User found successfully"))
                                .flatMap(response -> ServerResponse.ok().bodyValue(response));
                    } catch (ValueObjectException e) {
                        log.warn("Invalid document ID format: {}", documentIdValue);
                        return ServerResponse.badRequest()
                                .bodyValue(ApiResponse.error("Invalid document ID format: " + documentIdValue));
                    }
                })
                .orElse(ServerResponse.badRequest()
                        .bodyValue(ApiResponse.error("Document ID parameter is required")))
                .onErrorResume(throwable -> handleError(throwable, request.path()));
    }

    public Mono<ServerResponse> getUsersByRole(ServerRequest request) {
        return request.queryParam("role")
                .map(roleValue -> {
                    log.debug("Getting users by role: {}", roleValue);
                    return userUseCase.findUsersByRole(roleValue)
                            .map(UserMapper::toDTO)
                            .collectList()
                            .map(users -> ApiResponse.success(users, "Users found successfully"))
                            .flatMap(response -> ServerResponse.ok().bodyValue(response));
                })
                .orElse(ServerResponse.badRequest()
                        .bodyValue(ApiResponse.error("Role parameter is required")))
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

    public Mono<ServerResponse> checkUserExistsByDocumentId(ServerRequest request) {
        return request.queryParam("documentId")
                .map(documentIdValue -> {
                    log.debug("Checking if user exists with document ID: {}", documentIdValue);
                    try {
                        DocumentId documentId = new DocumentId(documentIdValue);
                        return userUseCase.userExistsByDocumentId(documentId)
                                .map(exists -> ApiResponse.success(exists, 
                                    exists ? "User exists" : "User does not exist"))
                                .flatMap(response -> ServerResponse.ok().bodyValue(response));
                    } catch (ValueObjectException e) {
                        log.warn("Invalid document ID format: {}", documentIdValue);
                        return ServerResponse.badRequest()
                                .bodyValue(ApiResponse.error("Invalid document ID format: " + documentIdValue));
                    }
                })
                .orElse(ServerResponse.badRequest()
                        .bodyValue(ApiResponse.error("Document ID parameter is required")))
                .onErrorResume(throwable -> handleError(throwable, request.path()));
    }

    public Mono<ServerResponse> checkUserExistsById(ServerRequest request) {
        return Mono.fromCallable(() -> request.pathVariable("id"))
                .flatMap(userId -> {
                    log.debug("Checking if user exists with ID: {}", userId);
                    
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

    public Mono<ServerResponse> authenticateUser(ServerRequest request) {
        return request.bodyToMono(LoginRequestDTO.class)
                .flatMap(dto -> {
                    log.debug("Authenticating user: {}", dto.email());
                    try {
                        Email email = new Email(dto.email());
                        return userUseCase.authenticateUser(email, dto.password())
                                .map(user -> {
                                    UserAuthDTO authDTO = UserMapper.toAuthDTO(user);
                                    String accessToken = jwtService.generateAccessToken(authDTO);
                                    String refreshToken = jwtService.generateRefreshToken(user.getId());
                                    return new LoginResponseDTO(
                                            accessToken,
                                            refreshToken,
                                            jwtService.getExpirationMs(),
                                            authDTO
                                    );
                                })
                                .map(response -> ApiResponse.success(response, "Authentication successful"))
                                .flatMap(response -> ServerResponse.ok().bodyValue(response));
                    } catch (ValueObjectException e) {
                        log.warn("Invalid email format: {}", dto.email());
                        return ServerResponse.badRequest()
                                .bodyValue(ApiResponse.error("Invalid email format"));
                    }
                })
                .onErrorResume(throwable -> handleError(throwable, request.path()));
    }

    private Mono<UserRequestDTO> validateCreateRequest(UserRequestDTO dto) {
        if (dto.firstName() == null || dto.firstName().trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("First name is required"));
        }
        if (dto.lastName() == null || dto.lastName().trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("Last name is required"));
        }
        if (dto.email() == null || dto.email().trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("Email is required"));
        }
        if (dto.password() == null || dto.password().trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("Password is required"));
        }
        if (dto.role() == null || dto.role().trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("Role is required"));
        }
        if (dto.baseSalary() == null) {
            return Mono.error(new InvalidUserDataException("Base salary is required"));
        }
        return Mono.just(dto);
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