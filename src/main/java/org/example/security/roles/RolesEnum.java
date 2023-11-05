package org.example.security.roles;

import lombok.Getter;

@Getter
public enum RolesEnum {
    USER("USER"),
    ADMIN("ADMIN");

    private final String message;

    RolesEnum(String message) {
        this.message = message;
    }
}
