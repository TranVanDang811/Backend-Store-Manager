package com.tranvandang.backend.mapper;


import com.tranvandang.backend.dto.request.PermissionRequest;
import com.tranvandang.backend.dto.response.PermissionResponse;
import com.tranvandang.backend.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
