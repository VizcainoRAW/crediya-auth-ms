package co.com.crediya.model.user;

public enum Role {
    ADMIN("ADMIN", "Administrator with full access"),
    USER("USER", "Regular user with standard access"),
    ADVISER("EMPLOYEE", "Company employee");

    private final String code;
    private final String description;

    Role(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Role fromCode(String code) {
        for (Role role : Role.values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role code: " + code);
    }

    public boolean hasElevatedPrivileges() {
        return this == ADMIN;
    }
}