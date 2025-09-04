// Value Object Exceptions
package co.com.crediya.model.valueobject.exception;

/**
 * Base exception for value object violations
 */
public abstract class InvalidDocumentId extends ValueObjectException {
    
    protected InvalidDocumentId(String message) {
        super(message);
    }
    
    protected InvalidDocumentId(String message, Throwable cause) {
        super(message, cause);
    }
}