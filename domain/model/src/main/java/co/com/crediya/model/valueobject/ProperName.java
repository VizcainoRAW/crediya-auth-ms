package co.com.crediya.model.valueobject;

import java.util.Objects;

import co.com.crediya.model.valueobject.exception.InvalidProperNameException;

public class ProperName {
    
    private final String value;
    
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 20;

    public ProperName(String value, String field){
        if(value == null || value.trim().isBlank()){
            throw new InvalidProperNameException(field);
        }

        if (value.length() < MIN_NAME_LENGTH) {
            throw new InvalidProperNameException(field, MIN_NAME_LENGTH, MAX_NAME_LENGTH);       
        }

        if (value.length() > MAX_NAME_LENGTH) {
            throw new InvalidProperNameException(field, MIN_NAME_LENGTH, MAX_NAME_LENGTH);
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
