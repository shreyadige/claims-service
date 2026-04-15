package com.claims.processing.claimsservice.constants;

public enum ClaimStatus {

    CREATED("Created"),
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");


    private final String status;

    ClaimStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
