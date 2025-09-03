// Value Object Exceptions
package co.com.crediya.model.valueobject.exception;

import co.com.crediya.model.common.exception.DomainException;

/**
 * Base exception for value object violations
 */
public abstract class ValueObjectException extends DomainException {
    
    protected ValueObjectException(String message) {
        super(message);
    }
    
    protected ValueObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}