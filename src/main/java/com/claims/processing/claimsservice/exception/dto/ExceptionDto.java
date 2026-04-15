package com.claims.processing.claimsservice.exception.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExceptionDto {

    private int errorCode;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private String path;

    public ExceptionDto(int errorCode, String error,String message, LocalDateTime timestamp, String path) {
        this.errorCode = errorCode;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }

}
