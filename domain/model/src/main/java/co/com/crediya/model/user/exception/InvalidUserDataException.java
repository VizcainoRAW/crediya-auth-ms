package co.com.crediya.model.user.exception;

/**
 * Exception thrown when user data is invalid
 */
public class InvalidUserDataException extends UserDomainException {
    
    public InvalidUserDataException(String message) {
        super(message);
    }
    
    public InvalidUserDataException(String field, String reason) {
        super("Invalid " + field + ": " + reason);
    }
    
    public InvalidUserDataException(String message, Throwable cause) {
        super(message, cause);
    }
}