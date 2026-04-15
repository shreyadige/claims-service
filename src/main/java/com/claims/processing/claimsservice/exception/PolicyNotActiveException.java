package com.claims.processing.claimsservice.exception;

public class PolicyNotActiveException extends RuntimeException{

    public PolicyNotActiveException(String message) {
        super(message);
    }

}
