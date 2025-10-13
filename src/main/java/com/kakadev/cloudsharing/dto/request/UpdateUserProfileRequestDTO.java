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
public class UpdateUserProfileRequestDTO {

    @NotBlank(message = "FIRST_NAME_REQUIRED")
    String firstName;

    @NotBlank(message = "LAST_NAME_REQUIRED")
    String lastName;

    @URL(message = "URL_INVALID")
    String avatarUrl;
}
