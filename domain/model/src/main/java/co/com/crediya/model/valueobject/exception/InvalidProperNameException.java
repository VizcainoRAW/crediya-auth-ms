package co.com.crediya.model.valueobject.exception;

/**
 * Exception thrown when proper name validation fails
 */
public class InvalidProperNameException extends ValueObjectException {
    
    public InvalidProperNameException(String field, String reason) {
        super("Invalid " + field + ": " + reason);
    }
    
    public InvalidProperNameException(String field, int minLength, int maxLength) {
        super("The field " + field + " must be between " + minLength + " and " + maxLength + " characters");
    }
    
    public InvalidProperNameException(String field) {
        super("The field " + field + " cannot be null or empty");
    }
}