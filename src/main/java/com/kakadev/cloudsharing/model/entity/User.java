package com.kakadev.cloudsharing.model.entity;

import com.kakadev.cloudsharing.model.enums.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Email(message = "EMAIL_INVALID")
    @Column(nullable = false, updatable = false)
    String email;

    String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AuthProvider provider;

    String providerId;

    @NotBlank(message = "FIRST_NAME_REQUIRED")
    @Column(nullable = false)
    String firstName;

    @NotBlank(message = "LAST_NAME_REQUIRED")
    @Column(nullable = false)
    String lastName;

    @URL(message = "URL_INVALID")
    String avatarUrl;

    Integer credits;

    Boolean isAccountVerified;

    String verifyOtp;

    Instant verifyOtpExpiry;

    String resetPasswordOtp;

    Instant resetPasswordOtpExpiry;

    @ManyToMany
    Set<Role> roles;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Builder.Default
    Instant updatedAt = Instant.now();

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
