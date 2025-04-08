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

// Validation và annotation liên quan
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

// Lombok để giảm code boilerplate
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// Spring web & security
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller cho REST API liên quan đến user
@Slf4j // Lombok: tự động tạo logger
@RestController // Đánh dấu đây là một REST controller
@RequestMapping("/users") // Mapping cho tất cả các endpoint bắt đầu bằng /users
@RequiredArgsConstructor // Lombok: tạo constructor cho các final fields
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // Thiết lập mặc định access level là private + final cho các field
public class UserController {

    UserService userService; // Service xử lý logic chính

    // Tạo user mới
    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    // Lấy danh sách user có phân trang
    @GetMapping
    ApiResponse<Page<UserResponse>> getUsers(
            @RequestParam(defaultValue = "1") int page, // page mặc định là 1
            @RequestParam(defaultValue = "5") int size) { // size mặc định là 5

        // In ra roles hiện tại từ authentication context
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<Page<UserResponse>>builder()
                .result(userService.getUsers(page - 1, size)) // trừ 1 vì Spring sử dụng page index từ 0
                .build();
    }

    // Tìm kiếm user theo keyword (có phân trang)
    @GetMapping("/search")
    public Page<User> searchUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return userService.searchUsers(keyword, page - 1, size);
    }

    // Lấy thông tin user theo id
    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") String userId) {
        return userService.getUser(userId);
    }

    // Lấy thông tin user đang đăng nhập
    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    // Cập nhật thông tin user theo id
    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    // Xoá user theo id
    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "User has been deleted";
    }

    // Cập nhật trạng thái user (ACTIVE, INACTIVE, NONE, etc.)
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

    // Đổi mật khẩu user theo id
    @PatchMapping("/{userId}/change-password")
    public UserResponse changePassword(
            @PathVariable String userId,
            @RequestBody ChangePasswordRequest request) {
        return userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
    }

    // Cập nhật role của user (VD: ADMIN, USER,...)
    @PatchMapping("/{userId}/role")
    public ApiResponse<UserResponse> updateRole(
            @PathVariable String userId,
            @RequestParam String roleName) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateRole(userId, roleName))
                .build();
    }

    // Kiểm tra username đã tồn tại hay chưa
    @GetMapping("/check-username")
    public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
        return ApiResponse.<Boolean>builder()
                .result(userService.isUsernameExist(username))
                .build();
    }

    // Kiểm tra email đã tồn tại hay chưa
    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmail(@RequestParam String email) {
        return ApiResponse.<Boolean>builder()
                .result(userService.isEmailExist(email))
                .build();
    }
}
