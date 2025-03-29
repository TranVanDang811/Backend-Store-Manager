package com.tranvandang.backend.mapper;


import com.tranvandang.backend.dto.request.RoleRequest;
import com.tranvandang.backend.dto.response.RoleResponse;
import com.tranvandang.backend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
