package com.claims.processing.claimsservice.exception;

import com.claims.processing.claimsservice.exception.dto.ExceptionDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ExceptionDto> buildResponse(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request) {

        ExceptionDto dto = new ExceptionDto(
                status.value(),
                error,
                message,
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(dto);
    }

    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleClaimNotFoundException(
            ClaimNotFoundException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Claim Not Found",
                ex.getMessage(),
                request);
    }

    @ExceptionHandler(InvalidClaimRequestException.class)
    public ResponseEntity<ExceptionDto> handleInvalidClaimRequestException(
            InvalidClaimRequestException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid Claim Request",
                ex.getMessage(),
                request);
    }

    @ExceptionHandler(PolicyNotActiveException.class)
    public ResponseEntity<ExceptionDto> handlePolicyNotActiveException(
            PolicyNotActiveException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Policy Not Active",
                ex.getMessage(),
                request);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ExceptionDto> handleServiceUnavailableException(
            ServiceUnavailableException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service Unavailable",
                ex.getMessage(),
                request);
    }

    @ExceptionHandler(PolicyNotFoundException.class)
    public ResponseEntity<ExceptionDto> handlePolicyNotFoundException(
            PolicyNotFoundException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Policy Not Found",
                ex.getMessage(),
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleGenericException(
            Exception ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Something went wrong. Please try again later.",
                request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleMethodNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                ex.getMessage(),
                request);
    }
}
