package com.tranvandang.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // System errors
    UNCATEGORIZED_EXCEPTION(6666, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVER_ERROR(111, "Error deleting photos on Cloudinary", HttpStatus.INTERNAL_SERVER_ERROR),
    TOKEN_GENERATION_FAILED(500, "Failed to generate token", HttpStatus.INTERNAL_SERVER_ERROR),

    // Validation errors
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    FIRSTNAME_INVALID(1003, "FirstName must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1013, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_JSON(400, "Invalid JSON format", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(400, "Incorrect old password provided", HttpStatus.BAD_REQUEST),
    INVALID_STATUS(404, "INVALID STATUS", HttpStatus.NOT_FOUND),
    INVALID_DISCOUNT_CODE(1009, "Invalid discount code", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY(400,"Invalid product quantity",HttpStatus.BAD_REQUEST),

    // Auth errors
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    FORBIDDEN(403, "You do not have permission to perform this action", HttpStatus.FORBIDDEN),

    // User-related errors
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(404, "User not existed", HttpStatus.NOT_FOUND),
    CANNOT_DELETE_THUMBNAIL(400, "Cannot delete profile picture. Please update your profile picture first.", HttpStatus.BAD_REQUEST),

    // Resource not found
    PRODUCT_NOT_EXISTED(404, "Product not existed", HttpStatus.NOT_FOUND),
    ORDER_NOT_EXISTED(404, "Order not existed", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_EXISTED(404, "Payment not existed", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_EXISTED(404, "Address not existed", HttpStatus.NOT_FOUND),
    RESOURCE_NOT_FOUND(404, "Image not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED(400, "Role does not exist", HttpStatus.BAD_REQUEST),
    DISCOUNT_NOT_FOUND_MESSAGE(404, "Discount not found with id:", HttpStatus.NOT_FOUND),
    IMPORT_ORDER_NOT_FOUND(404, "Import Order not found", HttpStatus.NOT_FOUND),
    SHIPPING_NOT_FOUND(404, "Shipping not found", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(404, "Cart not found.", HttpStatus.NOT_FOUND),
    SUPPLIER_NOT_FOUND(404, "Supplier not found.", HttpStatus.NOT_FOUND),
    SLIDER_NOT_FOUND(404, "Slider not found.", HttpStatus.NOT_FOUND),
    IMAGE_NOT_FOUND(404,"Image not found",HttpStatus.NOT_FOUND ),
    ROLE_NOT_FOUND(404,"Role not found",HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND(404,"Permission not found",HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(404,"Category not found",HttpStatus.NOT_FOUND),
    BRAND_NOT_FOUND(404,"Brand not found",HttpStatus.NOT_FOUND),
    ADDRESS_NOT_FOUND(404,"Address not found",HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR(500,"Failed to export import orders to Excel",HttpStatus.INTERNAL_SERVER_ERROR),
    // Order state errors
    ORDER_ALREADY_SHIPPED(400, "Cannot delete a shipped order.", HttpStatus.BAD_REQUEST),
    ORDER_NOT_CONFIRMED(400, "Order must be confirmed before shipping.", HttpStatus.BAD_REQUEST),
    CANNOT_CANCEL_ORDER(409,"Cannot cancel this order in its current state.",HttpStatus.CONFLICT),
    CANNOT_UPDATE_ORDER(409,"Cannot update order",HttpStatus.CONFLICT),
    // Supplier state errors
    SUPPLIER_NAME_ALREADY_EXISTS(409, "Supplier name already exists.", HttpStatus.CONFLICT),
    BAD_REQUEST(400, "Keyword must not be empty", HttpStatus.BAD_REQUEST),
    // Upload
    UPLOAD_FAILED(400, "Image upload failed", HttpStatus.BAD_REQUEST),
    //Slider
    //Payment
    INVALID_PAYMENT_STATUS(400,"Invalid payment status",HttpStatus.BAD_REQUEST),
    UNABLE_QRCODE(500,"Unable to generate QR code",HttpStatus.INTERNAL_SERVER_ERROR),
    //Address
    ADDRESS_NOT_BELONG_TO_USER(400, "Address does not belong to the user", HttpStatus.BAD_REQUEST);
    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
