package com.claims.processing.claimsservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "claims")
public class ClaimsProcessingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;

    @Column(name = "claim_number", nullable = false, unique = true,updatable = false)
    private String claimNumber;
    private String policyNumber;
    private String claimType;
    private BigDecimal claimAmount;
    private String claimStatus;
    private LocalDate claimDate;
    private String customerName;
    private String mobileNumber;
    private String email;
    //Generates getter and setter methods for all fields


}
