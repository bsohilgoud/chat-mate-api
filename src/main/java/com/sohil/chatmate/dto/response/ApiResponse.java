package com.sohil.chatmate.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String message;
    private T payload;
    private String path;
    private String timestamp;

    // This 1st <T> explicitly defines T as a type parameter for that method - tells Java that this method introduces its own generic type parameter T. This means T is scoped only to this method.
    public static <T> ResponseEntity<ApiResponse<T>> success(T payload, String path){
        ApiResponse<T> response = new ApiResponseBuilder<T>()
                .status(HttpStatus.SC_OK)
                .payload(payload)
                .timestamp(LocalDateTime.now().toString())
                .path(path)
                .build();

        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T payload, String message, String path){
        ApiResponse<T> response = new ApiResponseBuilder<T>()
                .status(HttpStatus.SC_OK)
                .payload(payload)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .path(path)
                .build();

        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(int status, T payload, String path){
        ApiResponse<T> response = new ApiResponseBuilder<T>()
                .status(status)
                .payload(payload)
                .timestamp(LocalDateTime.now().toString())
                .path(path)
                .build();

        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(int status, T payload, String message, String path){
        ApiResponse<T> response = new ApiResponseBuilder<T>()
                .status(status)
                .payload(payload)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .path(path)
                .build();

        return ResponseEntity.ok(response);
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
