package com.kakadev.cloudsharing.mapper;

import com.kakadev.cloudsharing.dto.request.CreateUserRequestDTO;
import com.kakadev.cloudsharing.dto.request.UpdateUserProfileRequestDTO;
import com.kakadev.cloudsharing.dto.response.UserResponseDTO;
import com.kakadev.cloudsharing.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(CreateUserRequestDTO userDto);
    UserResponseDTO toResponseDTO(User user);
    @Mapping(target = "id", ignore = true) // Ignore ID field during update
    void updateEntityFromDTO(@MappingTarget User existingUser, UpdateUserProfileRequestDTO updateUserRequestDTO);
}
