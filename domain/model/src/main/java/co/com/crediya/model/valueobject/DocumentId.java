package co.com.crediya.model.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

import co.com.crediya.model.valueobject.exception.InvalidDocumentIdException;

public class DocumentId {
    
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[A-Za-z0-9-]+$");
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 12;
    
    private final String value;

    public DocumentId(String value) throws InvalidDocumentIdException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDocumentIdException();
        }

        String trimmedValue = value.trim().toUpperCase();
        
        if (trimmedValue.length() < MIN_LENGTH) {
            throw new InvalidDocumentIdException(
                "Document ID must be at least " + MIN_LENGTH + " characters long"
            );
        }
        
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new InvalidDocumentIdException(
                "Document ID cannot exceed " + MAX_LENGTH + " characters"
            );
        }
        
        if (!ALPHANUMERIC_PATTERN.matcher(trimmedValue).matches()) {
            throw new InvalidDocumentIdException(
                "Document ID can only contain letters, numbers, and hyphens"
            );
        }
        
        this.value = trimmedValue;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValidDocumentId(String documentId) {
        if (documentId == null || documentId.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = documentId.trim();
        return trimmed.length() >= MIN_LENGTH && 
               trimmed.length() <= MAX_LENGTH && 
               ALPHANUMERIC_PATTERN.matcher(trimmed).matches();
    }

    public String getMaskedValue() {
        if (value.length() <= 4) {
            return "*".repeat(value.length());
        }
        
        String prefix = value.substring(0, 2);
        String suffix = value.substring(value.length() - 2);
        String middle = "*".repeat(value.length() - 4);
        
        return prefix + middle + suffix;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DocumentId that = (DocumentId) obj;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "DocumentId{" + getMaskedValue() + "}";
    }
    
}
