package com.claims.processing.claimsservice.exception;

public class InvalidClaimRequestException extends RuntimeException {

    public InvalidClaimRequestException(String message) {
        super(message);
    }
}
