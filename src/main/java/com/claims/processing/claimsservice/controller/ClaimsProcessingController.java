package com.claims.processing.claimsservice.controller;


import com.claims.processing.claimsservice.dto.ClaimsPageResponse;
import com.claims.processing.claimsservice.dto.ClaimsProcessingRequest;
import com.claims.processing.claimsservice.dto.ClaimsProcessingResponse;
import com.claims.processing.claimsservice.servicelayer.ClaimsProcessingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/claims/processing/service")
public class ClaimsProcessingController {

    @Autowired
    private ClaimsProcessingService claimsProcessingService;

    @PostMapping("/addClaims")
    public ClaimsProcessingResponse addClaims(@Valid @RequestBody ClaimsProcessingRequest claimsProcessingRequest) throws Exception {
        return claimsProcessingService.addClaims(claimsProcessingRequest);
    }

    @GetMapping("/search/{claimNumber}")
    public ClaimsProcessingResponse getClaims(@PathVariable String claimNumber) {
        return claimsProcessingService.getClaimsByClaimId(claimNumber);
    }


    @GetMapping("/search")
    public ResponseEntity<ClaimsPageResponse> getAllClaims(
            @RequestParam(defaultValue = "0")         int page,
            @RequestParam(defaultValue = "10")        int size,
            @RequestParam(defaultValue = "claimDate") String sortBy,
            @RequestParam(defaultValue = "DESC")      String direction,
            @RequestParam(required = false)           String status) {

        ClaimsPageResponse response = claimsProcessingService
                .getAllClaims(page, size, sortBy, direction, status);
        return ResponseEntity.ok(response);
    }
}
