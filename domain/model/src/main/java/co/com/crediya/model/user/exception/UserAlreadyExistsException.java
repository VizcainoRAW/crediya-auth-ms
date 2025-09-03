package co.com.crediya.model.user.exception;

/**
 * Exception thrown when trying to create a user that already exists
 */
public class UserAlreadyExistsException extends UserDomainException {
    
    public UserAlreadyExistsException(String email) {
        super("User already exists with email: " + email);
    }
    
    public UserAlreadyExistsException(String field, String value) {
        super("User already exists with " + field + ": " + value);
    }
}