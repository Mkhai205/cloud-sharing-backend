package com.kakadev.cloudsharing.service;

import com.kakadev.cloudsharing.dto.request.CreateUserRequestDTO;
import com.kakadev.cloudsharing.dto.response.CreateUserResponseDTO;
import org.springframework.stereotype.Service;

public interface UserService {

    public CreateUserResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO);
}
