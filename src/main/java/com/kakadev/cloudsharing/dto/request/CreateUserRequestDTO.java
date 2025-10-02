package com.kakadev.cloudsharing.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequestDTO {

    String email;

    String password;

    String firstName;

    String lastName;

    String avatarUrl;
}
