package com.kakadev.cloudsharing.model.enums;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public enum Role {
    ADMIN("ADMIN", "Administrator role"),
    USER("USER", "User role")
    ;
    String name;
    String description;

    Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
