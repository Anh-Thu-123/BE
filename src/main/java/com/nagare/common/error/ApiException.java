package com.nagare.common.error;

import java.util.List;
import org.springframework.http.HttpStatus;

/** Ngoai le nghiep vu chuan hoa, GlobalExceptionHandler chuyen thanh { code, message, fieldErrors[] }. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final List<FieldError> fieldErrors;

    public ApiException(HttpStatus status, String code, String message) {
        this(status, code, message, List.of());
    }

    public ApiException(HttpStatus status, String code, String message, List<FieldError> fieldErrors) {
        super(message);
        this.status = status;
        this.code = code;
        this.fieldErrors = fieldErrors;
    }

    public static ApiException notFound(String entity) {
        return new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", entity + " khong ton tai");
    }

    public static ApiException conflict(String code, String message) {
        return new ApiException(HttpStatus.CONFLICT, code, message);
    }

    public static ApiException badRequest(String code, String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, code, message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", message);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public List<FieldError> getFieldErrors() { return fieldErrors; }

    public record FieldError(String field, String message) {}
}
