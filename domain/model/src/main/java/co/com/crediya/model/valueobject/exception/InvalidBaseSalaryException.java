package co.com.crediya.model.valueobject.exception;

/**
 * Exception thrown when base salary validation fails
 */
public class InvalidBaseSalaryException extends ValueObjectException {
    
    public InvalidBaseSalaryException(String reason) {
        super("Invalid base salary: " + reason);
    }
    
    public InvalidBaseSalaryException() {
        super("Base salary cannot be null");
    }
}