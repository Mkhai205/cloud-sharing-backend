package com.kakadev.cloudsharing.exception;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    USER_EXISTED(1001, "Username already existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    PASSWORD_INVALID(1003, "Password must be between {min} and {max} characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1004, "User is not authenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "User is not authorized to perform this action", HttpStatus.FORBIDDEN),
    FIRST_NAME_REQUIRED(1006, "First name is required", HttpStatus.BAD_REQUEST),
    LAST_NAME_REQUIRED(1007, "Last name is required", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1008, "Email is not valid", HttpStatus.BAD_REQUEST),
    URL_INVALID(1009, "URL is not valid", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(1010, "Current password is incorrect", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(1011, "New password and confirm password do not match", HttpStatus.BAD_REQUEST),
    SEND_EMAIL_FAILED(2001, "Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR),
    VERIFICATION_CODE_INVALID(3001, "Verification code is invalid", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_EXPIRED(3002, "Verification code has expired", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_VERIFIED(3003, "Account is not verified", HttpStatus.FORBIDDEN),
    FILE_NOT_FOUND(4001, "File not found", HttpStatus.NOT_FOUND),
    FILE_SIZE_EXCEEDED(4002, "File size exceeded the limit of {max} MB", HttpStatus.BAD_REQUEST),
    USER_ALREADY_VERIFIED(5001, "User account is already verified", HttpStatus.BAD_REQUEST),
    RESET_PASSWORD_TOKEN_EXPIRED(5002, "Reset password token has expired", HttpStatus.BAD_REQUEST),
    INVALID_KEY(8888, "Invalid key provided", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    ;
    int code;
    String message;
    HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
