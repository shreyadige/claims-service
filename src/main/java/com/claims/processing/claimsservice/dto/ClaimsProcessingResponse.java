package com.claims.processing.claimsservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClaimsProcessingResponse {

    private String claimNumber;
    private String claimStatus;
    private String claimDate;
    private String customerName;
    private BigDecimal claimAmount;
    private String claimType;
    private String policyNumber;
    private String mobileNumber;
    private String email;
}
