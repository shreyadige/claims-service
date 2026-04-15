package com.claims.processing.claimsservice.events;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClaimCreatedEvent {

    private String claimNumber;
    private String claimStatus;
    private LocalDate claimDate;
    private BigDecimal claimAmount;
    private String policyNumber;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

}
