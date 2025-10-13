package com.kakadev.cloudsharing.model.enums;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public enum AuthProvider {
    LOCAL, GOOGLE
}
