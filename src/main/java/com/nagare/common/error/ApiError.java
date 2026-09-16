package com.nagare.common.error;

import java.util.List;

/** Hinh dang loi duy nhat tra ve tu API, theo muc 07: { code, message, fieldErrors[] }. */
public record ApiError(String code, String message, List<ApiException.FieldError> fieldErrors) {
    public static ApiError of(String code, String message) {
        return new ApiError(code, message, List.of());
    }
}
