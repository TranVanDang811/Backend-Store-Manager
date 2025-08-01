// Package và import các lớp cần thiết
package com.tranvandang.backend.controller;

// DTOs dùng cho request và response
import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.ChangePasswordRequest;
import com.tranvandang.backend.dto.request.UserCreationRequest;
import com.tranvandang.backend.dto.request.UserUpdateRequest;
import com.tranvandang.backend.dto.response.ProductResponse;
import com.tranvandang.backend.dto.response.UserResponse;

// Entity
import com.tranvandang.backend.entity.Product;
import com.tranvandang.backend.entity.User;

// Service xử lý nghiệp vụ liên quan tới User
import com.tranvandang.backend.service.UserService;

// Enum định nghĩa các trạng thái user
import com.tranvandang.backend.util.UserStatus;

// Validation and annotation
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

// Lombok
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// Spring web & security
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


// Controller REST API
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    // Create new user
    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    // Get a list of users with pagination
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

    // Search user by keyword (with pagination)
    @GetMapping("/search")
    ApiResponse<Page<User>> searchUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.<Page<User>>builder()
                .result(userService.searchUsers(keyword, page - 1, size))
                .build();
    }

    // Get user information by id
    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    // Get information of logged in user
    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    // Update user information by id
    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    // Delete user by id
    @DeleteMapping("/{userId}")
    ApiResponse<Void>deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<Void>builder().message("Delete successfully").build();
    }

    // Update user status (ACTIVE, INACTIVE, NONE, etc.)
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

    // Change user password by id
    @PatchMapping("/{userId}/change-password")
    public ApiResponse<Void> changePassword(
            @PathVariable String userId,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return ApiResponse.<Void>builder()
                .message("Password updated successfully")
                .build();
    }

    // Update user roles (eg: ADMIN, USER,...)
    @PatchMapping("/{userId}/role")
    public ApiResponse<UserResponse> updateRole(
            @PathVariable String userId,
            @RequestParam String roleName) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateRole(userId, roleName))
                .build();
    }

    // Check if username already exists
    @GetMapping("/check-username")
    public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
        return ApiResponse.<Boolean>builder()
                .result(userService.isUsernameExist(username))
                .build();
    }

    // Check if email exists
    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmail(@RequestParam String email) {
        return ApiResponse.<Boolean>builder()
                .result(userService.isEmailExist(email))
                .build();
    }
}
