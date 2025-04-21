package com.tranvandang.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(6666, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST), // loi cho biet sai gi do trong code
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    FIRSTNAME_INVALID(1003, "FirstName must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1013, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(404, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_JSON(400,"Invalid JSON format", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(404,"Product not existed", HttpStatus.NOT_FOUND),
    ORDER_NOT_EXISTED(404,"Order not existed", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_EXISTED(404,"Payment not existed", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_EXISTED(404,"Address not existed", HttpStatus.NOT_FOUND),
    FORBIDDEN(403,"You do not have permission to perform this action", HttpStatus.FORBIDDEN),
    INVALID_PASSWORD(400,"Incorrect old password provided", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(400,"Role does not exist", HttpStatus.BAD_REQUEST),
    UPLOAD_FAILED(400,"Image upload failed", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND(404,"Image not found", HttpStatus.NOT_FOUND),
    SERVER_ERROR(111,"Error deleting photos on Cloudinary",HttpStatus.INTERNAL_SERVER_ERROR),
    CANNOT_DELETE_THUMBNAIL(400,"Cannot delete profile picture. Please update your profile picture first.",HttpStatus.BAD_REQUEST),
    INVALID_STATUS(404,"INVALID STATUS",HttpStatus.NOT_FOUND)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
