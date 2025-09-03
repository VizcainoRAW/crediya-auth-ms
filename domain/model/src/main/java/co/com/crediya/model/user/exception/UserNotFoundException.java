package co.com.crediya.model.user.exception;

/**
 * Exception thrown when a user is not found
 */
public class UserNotFoundException extends UserDomainException {
    
    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId);
    }
    
    public UserNotFoundException(String field, String value) {
        super("User not found with " + field + ": " + value);
    }
}