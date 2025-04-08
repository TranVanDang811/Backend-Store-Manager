package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.request.UserCreationRequest;
import com.tranvandang.backend.dto.request.UserUpdateRequest;
import com.tranvandang.backend.dto.response.UserResponse;
import com.tranvandang.backend.entity.User;

import org.mapstruct.*;


@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface UserMapper {
    @Mapping(target = "addresses", source = "addresses", qualifiedByName = "toEntitySet")
    User toUser(UserCreationRequest request);

    @Mapping(target = "addresses", source = "addresses", qualifiedByName = "toResponseSet")
    UserResponse toUserResponse(User user);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "roles", ignore = true) // Bỏ qua roles khi update
    @Mapping(target = "addresses", ignore = true) // Không cập nhật địa chỉ khi update
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
