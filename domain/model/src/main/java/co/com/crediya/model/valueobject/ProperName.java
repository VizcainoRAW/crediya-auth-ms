package co.com.crediya.model.valueobject;

import java.util.Objects;

public class ProperName {
    
    private final String value;
    
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 20;

    public ProperName(String value, String field){
        if(value == null || value.trim().isBlank()){
            throw new IllegalArgumentException("The field " + field + " cannot be null or empty.");
        }

        if (value.length() < MIN_NAME_LENGTH) {
            throw new IllegalArgumentException("The field " + field + " cannot less than " + MIN_NAME_LENGTH + " characters.");            
        }

        if (value.length() > MAX_NAME_LENGTH) {
        throw new IllegalArgumentException("The field " + field + " cannot exceed " + MAX_NAME_LENGTH + " characters.");
        }

        this.value = capitalize(value.trim());
    }

    public String getValue() { return value; }

    private static String capitalize(String name) {
        String[] parts = name.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part.substring(0, 1).toUpperCase())
            .append(part.substring(1))
            .append(" ");
        }
        return sb.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProperName name = (ProperName) o;
        return Objects.equals(value, name.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
