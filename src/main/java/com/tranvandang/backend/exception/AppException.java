package com.tranvandang.backend.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String detailMessage) {
        super(errorCode.getMessage() + detailMessage);
        this.errorCode = errorCode;
    }

    private final ErrorCode errorCode;


}
