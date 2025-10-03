package com.kakadev.cloudsharing.dto.response;

import com.kakadev.cloudsharing.model.enums.AuthProvider;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDTO {
    UUID id;
    String email;
    AuthProvider provider;
    String providerId;
    String firstName;
    String lastName;
    String avatarUrl;
    Integer credits;
    Instant createdAt;
    Instant updatedAt;
}
