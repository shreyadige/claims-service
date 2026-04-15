package com.claims.processing.claimsservice.servicelayer;

import com.claims.processing.claimsservice.entity.ClaimSequence;
import com.claims.processing.claimsservice.repository.ClaimSequenceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ClaimNumberGenerator {

    @Autowired
    private ClaimSequenceRepository claimSequenceRepository;

    @Transactional
    public String generateclaimNumber(){
        int year = LocalDate.now().getYear();
        ClaimSequence claimSequence = claimSequenceRepository.findByYearWithLock(year).orElse(new ClaimSequence(year, 0));
        claimSequence.setLastSequence(claimSequence.getLastSequence() + 1);
        claimSequenceRepository.save(claimSequence);
        return String.format("CLM%d%05d", year, claimSequence.getLastSequence());

    }
}
