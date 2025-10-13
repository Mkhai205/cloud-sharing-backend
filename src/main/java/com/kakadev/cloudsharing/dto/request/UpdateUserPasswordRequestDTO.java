package com.kakadev.cloudsharing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UpdateUserPasswordRequestDTO {

    @NotBlank(message = "PASSWORD_INVALID")
    String currentPassword;

    @NotBlank(message = "PASSWORD_INVALID")
    String newPassword;

    @NotBlank(message = "PASSWORD_INVALID")
    String confirmPassword;
}
