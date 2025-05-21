package com.sohil.chatmate.dto;

import lombok.Builder;
import org.apache.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private String path;
    private String timestamp;

    // This 1st <T> explicitly defines T as a type parameter for that method - tells Java that this method introduces its own generic type parameter T. This means T is scoped only to this method.
    public static <T> ApiResponse<T> success(T data, String path){
        return new ApiResponseBuilder<T>()
                .status(HttpStatus.SC_OK)
                .data(data)
                .timestamp(LocalDateTime.now().toString())
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message, String path){
        return new ApiResponseBuilder<T>()
                .status(HttpStatus.SC_OK)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> success(int status, T data, String path){
        return new ApiResponseBuilder<T>()
                .status(status)
                .data(data)
                .timestamp(LocalDateTime.now().toString())
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> success(int status, T data, String message, String path){
        return new ApiResponseBuilder<T>()
                .status(status)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> error(int status, String message, String path){
        return new ApiResponseBuilder<T>()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .path(path)
                .build();
    }
}
