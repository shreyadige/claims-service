package com.claims.processing.claimsservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "claim_sequence")
public class ClaimSequence {

    @Id
    private Integer year;

    @Column(name = "last_sequence")
    private Integer lastSequence;

    public Integer getYear() {
        return year;
    }

    public Integer getLastSequence() {
        return lastSequence;
    }

    public void setLastSequence(Integer lastSequence) {
        this.lastSequence = lastSequence;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
    // constructor
    public ClaimSequence(Integer year, Integer lastSequence) {
        this.year = year;
        this.lastSequence = lastSequence;
    }

    public ClaimSequence() {
    }
}
