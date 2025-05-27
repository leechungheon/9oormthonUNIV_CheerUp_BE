package com.example.demo.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;


    /**
     * Creates a successful API response.
     *
     * @param <T> The type of the data included in the response.
     * @param data The data to include in the response.
     * @param message A message describing the success.
     * @return An instance of {@code ApiResponse} representing a successful response.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Creates an error API response.
     *
     * @param <T> The type of the data (always {@code null} for error responses).
     * @param message A message describing the error.
     * @return An instance of {@code ApiResponse} representing an error response.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
