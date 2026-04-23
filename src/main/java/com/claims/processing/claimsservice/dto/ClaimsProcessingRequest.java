package com.claims.processing.claimsservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClaimsProcessingRequest {

    private String policyNumber;

    private String claimType;

    private BigDecimal claimAmount;

    private String customerName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "\\+\\d{12}", message = "Mobile number must be a 10-digit number starting with country code (Ex.+919876543210)")
    private String mobileNumber;

    private String email;
}
