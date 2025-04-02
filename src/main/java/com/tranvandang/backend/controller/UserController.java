package com.tranvandang.backend.controller;


import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.UserCreationRequest;
import com.tranvandang.backend.dto.request.UserUpdateRequest;
import com.tranvandang.backend.dto.response.ProductResponse;
import com.tranvandang.backend.dto.response.UserResponse;
import com.tranvandang.backend.entity.Product;
import com.tranvandang.backend.entity.User;
import com.tranvandang.backend.service.UserService;
import com.tranvandang.backend.util.UserStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<UserResponse>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<Page<UserResponse>>builder()
                .result(userService.getUsers(page - 1, size))
                .build();
    }

    @GetMapping("/search")
    public Page<User> searchUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return userService.searchUsers(keyword, page - 1, size);
    }

    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") String userId) {
        return userService.getUser(userId);
    }


    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "User has been deleted";
    }

    @PatchMapping("/{userId}")
    public ApiResponse<UserResponse> changerStatus(
            @PathVariable String userId,
            @RequestParam UserStatus status) {

        log.info("Request changer user status, userId = {}", userId);

        UserResponse response = userService.changerStatus(userId, status);

        return ApiResponse.<UserResponse>builder()
                .result(response)
                .build();
    }
}
