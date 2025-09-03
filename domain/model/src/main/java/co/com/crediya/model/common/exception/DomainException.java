// Base Domain Exception
package co.com.crediya.model.common.exception;

/**
 * Base class for all domain exceptions
 */
public abstract class DomainException extends RuntimeException {
    
    protected DomainException(String message) {
        super(message);
    }
    
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}