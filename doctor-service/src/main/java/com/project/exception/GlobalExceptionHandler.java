package com.project.exception;

import com.project.dto.response.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDto> handleApiException(ApiException ex, HttpServletRequest request) {
        ErrorResponseDto response = buildError(ex.getCode(), ex.getMessage(), request);
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorResponseDto response = buildError("VALIDATION_ERROR", ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleDoctorNotFound(DoctorNotFoundException ex, HttpServletRequest request) {
        ErrorResponseDto response = buildError("DOCTOR_NOT_FOUND", ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpected(Exception ex, HttpServletRequest request) {
        ErrorResponseDto response = buildError("INTERNAL_ERROR", ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private ErrorResponseDto buildError(String code, String message, HttpServletRequest request) {
        ErrorResponseDto dto = new ErrorResponseDto();
        dto.setCode(code);
        dto.setMessage(message);
        dto.setTraceId(request.getHeader("X-Request-Id") != null ? request.getHeader("X-Request-Id") : "N/A");
        dto.setTimestamp(LocalDateTime.now().toString());
        return dto;
    }
}
