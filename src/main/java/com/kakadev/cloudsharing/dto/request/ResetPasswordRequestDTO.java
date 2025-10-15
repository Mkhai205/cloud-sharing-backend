package com.kakadev.cloudsharing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequestDTO {
    String resetPasswordToken;
    @NotBlank(message = "PASSWORD_INVALID")
    String newPassword;
    @NotBlank(message = "PASSWORD_INVALID")
    String confirmNewPassword;
}
