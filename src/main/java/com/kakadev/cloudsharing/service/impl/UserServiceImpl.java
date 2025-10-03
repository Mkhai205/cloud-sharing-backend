package com.kakadev.cloudsharing.service.impl;

import com.kakadev.cloudsharing.dto.request.CreateUserRequestDTO;
import com.kakadev.cloudsharing.dto.request.UpdateUserRequestDTO;
import com.kakadev.cloudsharing.dto.response.UserResponseDTO;
import com.kakadev.cloudsharing.exception.AppException;
import com.kakadev.cloudsharing.exception.ErrorCode;
import com.kakadev.cloudsharing.mapper.UserMapper;
import com.kakadev.cloudsharing.model.entity.User;
import com.kakadev.cloudsharing.model.enums.AuthProvider;
import com.kakadev.cloudsharing.repository.UserRepository;
import com.kakadev.cloudsharing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
        User newUser = userMapper.toEntity(createUserRequestDTO);

        newUser.setProvider(AuthProvider.LOCAL);
        newUser.setCredits(5);

        User savedUser = userRepository.save(newUser);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO getUserById(UUID userId) {
        return userMapper.toResponseDTO(userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UserResponseDTO getMyProfile() {
        // TODO: Implement authentication and get the current user
        return null;
    }

    @Override
    public UserResponseDTO updateUser(UUID userId, UpdateUserRequestDTO updateUserRequestDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateEntityFromDTO(existingUser, updateUserRequestDTO);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }
}
