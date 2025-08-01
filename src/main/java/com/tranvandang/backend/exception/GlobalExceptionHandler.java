package com.tranvandang.backend.exception;

import com.tranvandang.backend.dto.request.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice // Đánh dấu đây là class xử lý exception cho toàn bộ controller
@Slf4j // Lombok tự động tạo logger log
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min"; // Khóa dùng để lấy giá trị nhỏ nhất trong message validate

    // Xử lý tất cả exception không rõ loại - thường là RuntimeException hoặc Exception nói chung
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Object>> handlingRuntimeException(RuntimeException exception) {
        ApiResponse apiResponse = new ApiResponse(); // Tạo response chung
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode()); // Mã lỗi mặc định
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage()); // Thông báo lỗi mặc định

        return ResponseEntity.badRequest().body(apiResponse); // Trả về lỗi 400 Bad Request
    }

    // Xử lý các lỗi do mình định nghĩa (custom exception)
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Object>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode(); // Lấy mã lỗi từ exception
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode()); // Gán mã lỗi vào response
        apiResponse.setMessage(exception.getMessage()); // Gán thông báo lỗi vào response

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse); // Trả về status code tùy theo lỗi
    }

    // Xử lý lỗi truy cập không hợp lệ (ví dụ: không đủ quyền)
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED; // Lỗi truy cập trái phép

        return ResponseEntity.status(errorCode.getStatusCode()) // Trả về mã lỗi và thông báo tương ứng
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // Xử lý lỗi validate khi tham số đầu vào không hợp lệ (thường dùng @Valid)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage(); // Lấy key enum từ message validate (ví dụ: "INVALID_USERNAME")

        ErrorCode errorCode = ErrorCode.INVALID_KEY; // Mặc định dùng lỗi INVALID_KEY nếu không tìm thấy

        Map<String, Object> attributes = null; // Map để chứa thuộc tính validate (ví dụ: min=5)
        try {
            errorCode = ErrorCode.valueOf(enumKey); // Tìm lỗi tương ứng trong enum ErrorCode

            List<ObjectError> errors = exception.getBindingResult().getAllErrors(); // Lấy tất cả lỗi validate
            if (!errors.isEmpty()) {
                var constraintViolation =
                        errors.get(0).unwrap(ConstraintViolation.class); // Ép kiểu để lấy ConstraintViolation

                attributes =
                        constraintViolation.getConstraintDescriptor().getAttributes(); // Lấy thuộc tính validate (ví dụ: min, max)

                log.info("Validation attributes: {}", attributes); // Ghi log để debug
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid error code: {}", enumKey, e); // Nếu enumKey không tồn tại trong ErrorCode
        } catch (Exception e) {
            log.error("Failed to extract constraint violation attributes", e); // Lỗi không xác định khi unwrap
        }

        ApiResponse apiResponse = new ApiResponse(); // Tạo response trả về client

        // Nếu lấy được attributes thì map vào message, ví dụ: "Giá trị tối thiểu là {min}" -> "Giá trị tối thiểu là 5"
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse); // Trả về lỗi 400 Bad Request
    }

    // Hàm thay thế biến {min} trong message bằng giá trị thực tế từ attributes
    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE)); // Lấy giá trị "min" từ map
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue); // Thay thế {min} bằng giá trị thực tế trong thông báo
    }
}
