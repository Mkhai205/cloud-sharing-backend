package com.kakadev.cloudsharing.controller;

import com.kakadev.cloudsharing.dto.request.CreateUserRequestDTO;
import com.kakadev.cloudsharing.dto.request.UpdateUserPasswordRequestDTO;
import com.kakadev.cloudsharing.dto.request.UpdateUserProfileRequestDTO;
import com.kakadev.cloudsharing.dto.response.ApiResponse;
import com.kakadev.cloudsharing.dto.response.UserResponseDTO;
import com.kakadev.cloudsharing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @RequestBody @Valid CreateUserRequestDTO createUserRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponseDTO>builder()
                    .result(userService.createUser(createUserRequestDTO))
                    .build());
    }

    @GetMapping
    public ApiResponse<List<UserResponseDTO>> getAllUsers() {
        return ApiResponse.<List<UserResponseDTO>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponseDTO> getUserById(
            @PathVariable("userId") UUID userId
    ) {
        return ApiResponse.<UserResponseDTO>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<UserResponseDTO> getMyProfile() {
        return ApiResponse.<UserResponseDTO>builder()
                .result(userService.getMyProfile())
                .build();
    }

    @PutMapping("/profile/{userId}")
    public ApiResponse<UserResponseDTO> updateUserProfile(
            @PathVariable("userId") UUID userId,
            @RequestBody @Valid UpdateUserProfileRequestDTO updateUserRequestDTO
    )  {
        return ApiResponse.<UserResponseDTO>builder()
                .result(userService.updateUserProfile(userId, updateUserRequestDTO))
                .build();
    }

    @PutMapping("/password/{userId}")
    public ApiResponse<UserResponseDTO> updateUserPassword(
            @PathVariable("userId") UUID userId,
            @RequestBody @Valid UpdateUserPasswordRequestDTO updateUserPasswordRequestDTO
            )  {
        return ApiResponse.<UserResponseDTO>builder()
                .result(userService.updateUserPassword(userId, updateUserPasswordRequestDTO))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(
            @PathVariable("userId") UUID userId
    ) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .message("User with id " + userId + " deleted successfully.")
                .build();
    }
}
