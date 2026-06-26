package com.scholarflow.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

/**
 * Generic API response wrapper for consistent response format.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private Instant timestamp;
    private int status;
    private String message;
    private T data;
    private Object errors;
    private String path;
    private boolean success;

    public static <T> ApiResponse<T> success(T data, String message, int status) {
        return ApiResponse.<T>builder()
                .timestamp(Instant.now())
                .status(status)
                .message(message)
                .data(data)
                .success(true)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Success", 200);
    }

    public static <T> ApiResponse<T> created(T data) {
        return success(data, "Created successfully", 201);
    }

    public static <T> ApiResponse<T> error(String message, int status, String path, Object errors) {
        return ApiResponse.<T>builder()
                .timestamp(Instant.now())
                .status(status)
                .message(message)
                .path(path)
                .errors(errors)
                .success(false)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int status) {
        return error(message, status, null, null);
    }
}
