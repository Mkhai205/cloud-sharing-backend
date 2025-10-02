package com.kakadev.cloudsharing.mapper;

import com.kakadev.cloudsharing.dto.request.CreateUserRequestDTO;
import com.kakadev.cloudsharing.dto.response.CreateUserResponseDTO;
import com.kakadev.cloudsharing.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(CreateUserRequestDTO userDto);
    CreateUserResponseDTO toResponseDTO(User user);
}
