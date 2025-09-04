package co.com.crediya.model.valueobject;

import co.com.crediya.model.valueobject.exception.InvalidPasswordException;
import java.util.Objects;
import java.util.regex.Pattern;

public class Password {

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 128;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    private final String value;

    public Password(String value) throws InvalidPasswordException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidPasswordException();
        }
        
        String trimmedValue = value.trim();
        
        if (trimmedValue.length() < PASSWORD_MIN_LENGTH) {
            throw new InvalidPasswordException("Password must be at least 8 characters long");
        }
        
        if (trimmedValue.length() > PASSWORD_MAX_LENGTH) {
            throw new InvalidPasswordException("Password cannot exceed 128 characters");
        }
        
        if (!PASSWORD_PATTERN.matcher(trimmedValue).matches()) {
            throw new InvalidPasswordException(
                "Password must contain at least one lowercase letter, one uppercase letter, " +
                "one digit, and one special character (@$!%*?&)"
            );
        }
        
        this.value = trimmedValue;
    }

    // Constructor for already hashed passwords (from database)
    public static Password fromHash(String hashedValue) {
        return new Password() {
            @Override
            public String getValue() {
                return hashedValue;
            }
            
            @Override
            public boolean isHashed() {
                return true;
            }
        };
    }
    
    // Private constructor for fromHash method
    private Password() {
        this.value = null;
    }

    public String getValue() {
        return value;
    }

    public boolean isHashed() {
        return false; // Raw passwords are not hashed by default
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = password.trim();
        return trimmed.length() >= 8 && 
               trimmed.length() <= 128 && 
               PASSWORD_PATTERN.matcher(trimmed).matches();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Password password = (Password) obj;
        return Objects.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Password{[PROTECTED]}";
    }
}