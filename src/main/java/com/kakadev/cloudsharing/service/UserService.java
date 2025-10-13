package com.kakadev.cloudsharing.service;

import com.kakadev.cloudsharing.dto.request.CreateUserRequestDTO;
import com.kakadev.cloudsharing.dto.request.UpdateUserPasswordRequestDTO;
import com.kakadev.cloudsharing.dto.request.UpdateUserProfileRequestDTO;
import com.kakadev.cloudsharing.dto.response.UserResponseDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO);
    UserResponseDTO getUserById(UUID userId);
    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getMyProfile();

    UserResponseDTO updateUserProfile(UUID userId, UpdateUserProfileRequestDTO updateUserRequestDTO);

    UserResponseDTO updateUserPassword(UUID userId, @Valid UpdateUserPasswordRequestDTO updateUserPasswordRequestDTO);

    void deleteUser(UUID userId);
}
