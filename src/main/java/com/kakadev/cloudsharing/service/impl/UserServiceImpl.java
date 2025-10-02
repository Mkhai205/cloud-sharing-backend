package com.kakadev.cloudsharing.service.impl;

import com.kakadev.cloudsharing.dto.request.CreateUserRequestDTO;
import com.kakadev.cloudsharing.dto.response.CreateUserResponseDTO;
import com.kakadev.cloudsharing.mapper.UserMapper;
import com.kakadev.cloudsharing.model.entity.User;
import com.kakadev.cloudsharing.model.enums.AuthProvider;
import com.kakadev.cloudsharing.repository.UserRepository;
import com.kakadev.cloudsharing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public CreateUserResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
        User newUser = userMapper.toEntity(createUserRequestDTO);

        newUser.setProvider(AuthProvider.LOCAL);
        newUser.setCredits(5);

        User savedUser = userRepository.save(newUser);
        return userMapper.toResponseDTO(savedUser);
    }
}
