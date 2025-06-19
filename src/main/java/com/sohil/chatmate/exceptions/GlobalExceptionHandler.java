package com.sohil.chatmate.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sohil.chatmate.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTransientException;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        log.warn("User not found - Method: {}, Endpoint: {}, Message: {}", method, endpoint, ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        log.warn("Username not found - Method: {}, Endpoint: {}, Message: {}", method, endpoint, ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameNotFoundException(UsernameAlreadyExistsException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        log.warn("Username already exists - Method: {}, Endpoint: {}, Message: {}", method, endpoint, ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.CONFLICT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        String remoteAddr = getClientIpAddress(request);

        log.warn("Authentication failed - Method: {}, Endpoint: {}, IP: {}, Message: {}",
                method, endpoint, remoteAddr, ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(HttpStatus.FORBIDDEN.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        StringBuilder errorMessage = new StringBuilder("Constraint violation: ");
        ex.getConstraintViolations().forEach(violation ->
                errorMessage.append(violation.getPropertyPath()).append(" - ").append(violation.getMessage()).append("; ")
        );

        log.warn("Constraint violation - Method: {}, Endpoint: {}, Violations: {}", method, endpoint, errorMessage.toString());

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), errorMessage.toString(), endpoint);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        log.warn("Method not supported - Method: {}, Endpoint: {}, Supported methods: {}",
                method, endpoint, Arrays.toString(ex.getSupportedMethods()));

        String message = String.format("HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
                method, Arrays.toString(ex.getSupportedMethods()));

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.METHOD_NOT_ALLOWED.value(), message, endpoint);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        String contentType = request.getContentType();

        log.warn("Media type not supported - Method: {}, Endpoint: {}, Content-Type: {}, Supported types: {}",
                method, endpoint, contentType, ex.getSupportedMediaTypes());

        String message = String.format("Content type '%s' is not supported. Supported types: %s",
                contentType, ex.getSupportedMediaTypes());

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), message, endpoint);
        return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        log.warn("Missing request parameter - Method: {}, Endpoint: {}, Parameter: {} ({})",
                method, endpoint, ex.getParameterName(), ex.getParameterType());

        String message = String.format("Required parameter '%s' of type '%s' is missing",
                ex.getParameterName(), ex.getParameterType());

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message, endpoint);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        log.warn("Malformed JSON request - Method: {}, Endpoint: {}, Error: {}", method, endpoint, ex.getMessage());

        String message = "Malformed JSON request";
        if (ex.getCause() instanceof JsonParseException) {
            message = "Invalid JSON format";
        } else if (ex.getCause() instanceof JsonMappingException) {
            message = "JSON mapping error";
        }

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message, endpoint);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        String remoteAddr = getClientIpAddress(request);

        log.warn("Access denied - Method: {}, Endpoint: {}, IP: {}, Message: {}",
                method, endpoint, remoteAddr, ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", endpoint);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        log.error("Data integrity violation - Method: {}, Endpoint: {}, Error: {}", method, endpoint, ex.getMessage(), ex);

        String message = "Data integrity violation";
        if (ex.getCause() instanceof ConstraintViolationException) {
            message = "Database constraint violation";
        }

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.CONFLICT.value(), message, endpoint);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataAccessException(DataAccessException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        log.error("Database access error - Method: {}, Endpoint: {}, Error: {}", method, endpoint, ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Database operation failed", endpoint);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ")
        );

        log.warn("Validation failed - Method: {}, Endpoint: {}, Errors: {}", method, endpoint, errorMessage.toString());

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), errorMessage.toString(), endpoint);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiResponse<Object>> handleSQLException(SQLException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString().substring(0, 8);

        log.error("SQL Exception [ID: {}] - Method: {}, Endpoint: {}, SQL State: {}, Error Code: {}, Message: {}",
                errorId, method, endpoint, ex.getSQLState(), ex.getErrorCode(), ex.getMessage(), ex);

        // Handle specific SQL error codes
        String userMessage = "Database operation failed";
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        // Common SQL error patterns
        if (ex.getSQLState() != null) {
            String sqlState = ex.getSQLState();

            // Integrity constraint violations (23xxx)
            if (sqlState.startsWith("23")) {
                userMessage = "Data constraint violation";
                httpStatus = HttpStatus.CONFLICT;

                if (sqlState.equals("23505") || ex.getMessage().toLowerCase().contains("duplicate")) {
                    userMessage = "Duplicate entry - record already exists";
                } else if (sqlState.equals("23503") || ex.getMessage().toLowerCase().contains("foreign key")) {
                    userMessage = "Referenced data does not exist";
                } else if (sqlState.equals("23502") || ex.getMessage().toLowerCase().contains("not null")) {
                    userMessage = "Required field cannot be empty";
                }
            }
            // Syntax errors (42xxx)
            else if (sqlState.startsWith("42")) {
                log.error("SQL Syntax Error [ID: {}] - This indicates a programming error", errorId);
                userMessage = "Database query error";
            }
            // Connection errors (08xxx)
            else if (sqlState.startsWith("08")) {
                log.error("Database Connection Error [ID: {}] - Connection issue detected", errorId);
                userMessage = "Database connection unavailable";
                httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            }
            // Transaction rollback (40xxx)
            else if (sqlState.startsWith("40")) {
                log.warn("Transaction Rollback [ID: {}] - Deadlock or serialization failure", errorId);
                userMessage = "Transaction conflict - please retry";
                httpStatus = HttpStatus.CONFLICT;
            }
        }

        // Check for specific database error codes (MySQL examples)
        int errorCode = ex.getErrorCode();
        switch (errorCode) {
            case 1062: // MySQL: Duplicate entry
                userMessage = "Duplicate entry - record already exists";
                httpStatus = HttpStatus.CONFLICT;
                break;
            case 1452: // MySQL: Cannot add or update a child row (foreign key constraint)
                userMessage = "Referenced data does not exist";
                httpStatus = HttpStatus.CONFLICT;
                break;
            case 1048: // MySQL: Column cannot be null
                userMessage = "Required field cannot be empty";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case 1146: // MySQL: Table doesn't exist
                log.error("Database Schema Error [ID: {}] - Table doesn't exist", errorId);
                userMessage = "Database schema error";
                break;
            case 2003: // MySQL: Can't connect to server
            case 2006: // MySQL: Server has gone away
                log.error("Database Connection Error [ID: {}] - Server connectivity issue", errorId);
                userMessage = "Database service unavailable";
                httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
                break;
        }

        ApiResponse<Object> response = ApiResponse.error(
                httpStatus.value(),
                String.format("%s. Error ID: %s", userMessage, errorId),
                endpoint);
        return new ResponseEntity<>(response, httpStatus);
    }

    @ExceptionHandler(SQLTimeoutException.class)
    public ResponseEntity<ApiResponse<Object>> handleSQLTimeoutException(SQLTimeoutException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString().substring(0, 8);

        log.error("SQL Timeout Exception [ID: {}] - Method: {}, Endpoint: {}, Message: {}",
                errorId, method, endpoint, ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.REQUEST_TIMEOUT.value(),
                String.format("Database operation timed out. Error ID: %s", errorId),
                endpoint);
        return new ResponseEntity<>(response, HttpStatus.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleSQLIntegrityConstraintViolationException(
            SQLIntegrityConstraintViolationException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        log.warn("SQL Integrity Constraint Violation - Method: {}, Endpoint: {}, Message: {}",
                method, endpoint, ex.getMessage());

        String userMessage = "Data constraint violation";
        String errorMessage = ex.getMessage().toLowerCase();

        if (errorMessage.contains("duplicate") || errorMessage.contains("unique")) {
            userMessage = "Duplicate entry - record already exists";
        } else if (errorMessage.contains("foreign key")) {
            userMessage = "Referenced data does not exist";
        } else if (errorMessage.contains("not null")) {
            userMessage = "Required field cannot be empty";
        }

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.CONFLICT.value(), userMessage, endpoint);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ResponseEntity<ApiResponse<Object>> handleSQLSyntaxErrorException(SQLSyntaxErrorException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString().substring(0, 8);

        log.error("SQL Syntax Error [ID: {}] - Method: {}, Endpoint: {}, Message: {} - THIS INDICATES A PROGRAMMING ERROR",
                errorId, method, endpoint, ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                String.format("Database query error. Error ID: %s", errorId),
                endpoint);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SQLTransientException.class)
    public ResponseEntity<ApiResponse<Object>> handleSQLTransientException(SQLTransientException ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString().substring(0, 8);

        log.warn("SQL Transient Exception [ID: {}] - Method: {}, Endpoint: {}, Message: {} - Operation can be retried",
                errorId, method, endpoint, ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                String.format("Database temporarily unavailable - please retry. Error ID: %s", errorId),
                endpoint);
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        log.warn("Server Error - Method: {}, Endpoint: {}, Message: {}, Trace: {}", method, endpoint, ex.getMessage(), ex.getStackTrace());
        ex.printStackTrace();

        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: \n\n" + ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * Get client IP address from request, considering proxy headers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
