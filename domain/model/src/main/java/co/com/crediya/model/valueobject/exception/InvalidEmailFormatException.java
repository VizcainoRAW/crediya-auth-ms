package co.com.crediya.model.valueobject.exception;

/**
 * Exception thrown when email format is invalid
 */
public class InvalidEmailFormatException extends ValueObjectException {
    
    public InvalidEmailFormatException(String email) {
        super("Invalid email format: " + email);
    }
    
    public InvalidEmailFormatException() {
        super("Email cannot be null or empty");
    }
}