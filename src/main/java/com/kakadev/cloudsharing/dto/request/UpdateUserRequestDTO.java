package com.kakadev.cloudsharing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UpdateUserRequestDTO {

    @Size(min = 6, max = 30, message = "PASSWORD_INVALID")
    String password;

    @NotBlank(message = "FIRST_NAME_REQUIRED")
    String firstName;

    @NotBlank(message = "LAST_NAME_REQUIRED")
    String lastName;

    @URL(message = "URL_INVALID")
    String avatarUrl;
}
