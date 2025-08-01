package com.tranvandang.backend.configuration;


import com.tranvandang.backend.constant.PredefinedPermission;
import com.tranvandang.backend.constant.PredefinedRole;
import com.tranvandang.backend.entity.Permission;
import com.tranvandang.backend.entity.Role;
import com.tranvandang.backend.entity.User;
import com.tranvandang.backend.repository.PermissionRepository;
import com.tranvandang.backend.repository.RoleRepository;
import com.tranvandang.backend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    String convertToDescription(String name) {
        // Ví dụ: "USER_CREATE" -> "User Create"
        String[] words = name.split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }
        return builder.toString().trim();
    }


    @Value("${app.admin.username:admin}")
    String adminUsername;

    @Value("${app.admin.password:admin}")
    String adminPassword;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository,  PermissionRepository permissionRepository) {
        log.info("Initializing application.....");
        return args -> {
            // 1. Tạo danh sách tên permissions từ enum
            var allPermissionNames =
                    Arrays.stream(PredefinedPermission.values())
                            .map(Enum::name)
                            .toList();

            // 2. Lấy tất cả permissions đã có trong DB
            var existingPermissions = permissionRepository.findByNameIn(allPermissionNames);
            var existingPermissionNames = existingPermissions.stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());

            // 3. Tạo các permission còn thiếu
            List<Permission> toCreate = allPermissionNames.stream()
                    .filter(name -> !existingPermissionNames.contains(name))
                    .map(name -> Permission.builder()
                            .name(name)
                            .description(convertToDescription(name))
                            .build())
                    .toList();

            if (!toCreate.isEmpty()) {
                permissionRepository.saveAll(toCreate);
                existingPermissions.addAll(toCreate);
            }

            Role employeeRole = roleRepository.findByName(PredefinedRole.EMPLOYEE_ROLE)
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name(PredefinedRole.EMPLOYEE_ROLE)
                            .description("Employee role")
                            .build()));

            Role adminRole = roleRepository.findByName(PredefinedRole.ADMIN_ROLE)
                    .orElseGet(() -> {
                        Role newAdminRole = Role.builder()
                                .name(PredefinedRole.ADMIN_ROLE)
                                .description("Admin role")
                                .permissions(new HashSet<>(existingPermissions)) // Gán toàn bộ permissions
                                .build();
                        return roleRepository.save(newAdminRole);
                    });

            // 5. Tạo admin user nếu chưa có
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User user = User.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .roles(Set.of(adminRole, employeeRole)) // gán role
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }

            log.info("Application initialization completed .....");
        };
    }
}
