package com.claims.processing.claimsservice.repository;

import com.claims.processing.claimsservice.dto.ClaimsProcessingResponse;
import com.claims.processing.claimsservice.entity.ClaimsProcessingEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimsProcessingReposiory extends JpaRepository<ClaimsProcessingEntity, String> {

    @Query("SELECT c FROM ClaimsProcessingEntity c WHERE c.claimNumber = :claimNumber")
    Optional<ClaimsProcessingEntity> findByClaimNumber(@Param("claimNumber") String claimNumber);


    Page<ClaimsProcessingEntity> findAll(Pageable pageable);

    Page<ClaimsProcessingEntity> findByClaimStatus(String status, Pageable pageable);
}
