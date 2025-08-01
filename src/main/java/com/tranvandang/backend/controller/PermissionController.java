package com.tranvandang.backend.controller;


import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.PermissionRequest;
import com.tranvandang.backend.dto.response.PermissionResponse;
import com.tranvandang.backend.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<PermissionResponse> getById(@PathVariable String id) {
        return ApiResponse.<PermissionResponse>builder().result(permissionService.getById(id)).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<PermissionResponse> update(@PathVariable String id,
                                                     @RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder().result(permissionService.update(id, request)).build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable String permission) {
        permissionService.delete(permission);
        return ApiResponse.<Void>builder().build();
    }
}
