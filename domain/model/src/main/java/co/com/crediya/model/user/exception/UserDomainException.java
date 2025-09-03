// User Domain Exceptions
package co.com.crediya.model.user.exception;

import co.com.crediya.model.common.exception.DomainException;

/**
 * Base exception for user-related domain violations
 */
public abstract class UserDomainException extends DomainException {
    
    protected UserDomainException(String message) {
        super(message);
    }
    
    protected UserDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}