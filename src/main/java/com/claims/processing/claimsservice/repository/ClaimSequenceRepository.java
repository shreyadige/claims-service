package com.claims.processing.claimsservice.repository;

import com.claims.processing.claimsservice.entity.ClaimSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimSequenceRepository extends JpaRepository<ClaimSequence, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cs FROM ClaimSequence cs WHERE cs.year = :year")
    Optional<ClaimSequence> findByYearWithLock(@Param("year") Integer year);
}
