package com.kakadev.cloudsharing.exception;

import com.kakadev.cloudsharing.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String[] ATTRIBUTE = {"min", "max"};

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handleException(Exception exception) {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

        log.error("An uncategorized exception occurred: {}", exception.getMessage(), exception);

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        log.error("An uncategorized exception occurred: {}", exception.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        ErrorCode errorCode;

        Map<String, Object> attributes = null;

        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation = exception.getBindingResult()
                    .getAllErrors().getFirst().unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();

            log.info(attributes.toString());
        } catch (IllegalArgumentException e) {
            errorCode = ErrorCode.INVALID_KEY; // Fallback to a default error code
        }

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(Objects.nonNull(attributes)
                                ? mapAttribute(errorCode.getMessage(), attributes)
                                : errorCode.getMessage())
                        .build()
        );
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        for (String attribute : ATTRIBUTE) {
            if (attributes.containsKey(attribute)) {
                message = message.replace("{" + attribute + "}", String.valueOf(attributes.get(attribute)));
            }
        }
        return message;
    }
}
