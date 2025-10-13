package com.kakadev.cloudsharing.service.impl;

import com.kakadev.cloudsharing.dto.request.CreateUserRequestDTO;
import com.kakadev.cloudsharing.dto.request.UpdateUserPasswordRequestDTO;
import com.kakadev.cloudsharing.dto.request.UpdateUserProfileRequestDTO;
import com.kakadev.cloudsharing.dto.response.UserResponseDTO;
import com.kakadev.cloudsharing.exception.AppException;
import com.kakadev.cloudsharing.exception.ErrorCode;
import com.kakadev.cloudsharing.mapper.UserMapper;
import com.kakadev.cloudsharing.model.entity.Role;
import com.kakadev.cloudsharing.model.entity.User;
import com.kakadev.cloudsharing.model.enums.AuthProvider;
import com.kakadev.cloudsharing.repository.RoleRepository;
import com.kakadev.cloudsharing.repository.UserRepository;
import com.kakadev.cloudsharing.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO createUser(
            CreateUserRequestDTO createUserRequestDTO
    ) {
        User newUser = userMapper.toEntity(createUserRequestDTO);

        Set<Role> roles = new HashSet<>();
        roleRepository.findById(com.kakadev.cloudsharing.model.enums.Role.USER.getName())
                .ifPresent(roles::add);

        newUser.setRoles(roles);
        newUser.setProvider(AuthProvider.LOCAL);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setCredits(5);

        User savedUser = userRepository.save(newUser);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO getUserById(
            UUID userId
    ) {
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
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUserProfile(
            UUID userId, UpdateUserProfileRequestDTO updateUserRequestDTO
    ) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateEntityFromDTO(existingUser, updateUserRequestDTO);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public UserResponseDTO updateUserPassword(
            UUID userId, UpdateUserPasswordRequestDTO updateUserPasswordRequestDTO
    ) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(
                updateUserPasswordRequestDTO.getCurrentPassword(), existingUser.getPassword()
        )) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        if (!updateUserPasswordRequestDTO.getNewPassword().equals(
                updateUserPasswordRequestDTO.getConfirmPassword()
        )) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        existingUser.setPassword(passwordEncoder.encode(updateUserPasswordRequestDTO.getNewPassword()));
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }
}
