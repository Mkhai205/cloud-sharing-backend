package com.kakadev.cloudsharing.model.entity;

import com.kakadev.cloudsharing.model.enums.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    String email;

    String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AuthProvider provider;

    String providerId;

    @NotBlank(message = "First name cannot be blank")
    @Column(nullable = false)
    String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Column(nullable = false)
    String lastName;

    String avatarUrl;

    Integer credits;

    @Column(nullable = false, updatable = false)
    Instant createdAt;

    @Column(nullable = false)
    Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
