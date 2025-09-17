package co.com.crediya.api.Exception;

import java.sql.Date;

public class TokenExpiredException extends JwtValidationException {
    private final Date expiredAt;
    
    public TokenExpiredException(String message) {
        super(message);
        this.expiredAt = null;
    }
    
    public TokenExpiredException(String message, Date expiredAt) {
        super(message);
        this.expiredAt = expiredAt;
    }
    
    public Date getExpiredAt() {
        return expiredAt;
    }
}