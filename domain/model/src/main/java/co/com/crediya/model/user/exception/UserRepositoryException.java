// Repository Exceptions
package co.com.crediya.model.user.exception;

/**
 * Exception thrown when repository operations fail
 */
public class UserRepositoryException extends UserDomainException {
    
    public UserRepositoryException(String message) {
        super("Repository operation failed: " + message);
    }
    
    public UserRepositoryException(String message, Throwable cause) {
        super("Repository operation failed: " + message, cause);
    }
    
    public UserRepositoryException(String operation, String userId) {
        super("Failed to " + operation + " user with ID: " + userId);
    }
    
    public UserRepositoryException(String operation, String userId, Throwable cause) {
        super("Failed to " + operation + " user with ID: " + userId, cause);
    }
}