package com.claims.processing.claimsservice.servicelayer;


import com.claims.processing.claimsservice.constants.ClaimStatus;
import com.claims.processing.claimsservice.dto.ClaimsPageResponse;
import com.claims.processing.claimsservice.dto.ClaimsProcessingRequest;
import com.claims.processing.claimsservice.dto.ClaimsProcessingResponse;
import com.claims.processing.claimsservice.dto.PolicyStatusDto;
import com.claims.processing.claimsservice.entity.ClaimsProcessingEntity;
import com.claims.processing.claimsservice.events.ClaimCreatedEvent;
import com.claims.processing.claimsservice.events.ClaimEventProducer;
import com.claims.processing.claimsservice.exception.ClaimNotFoundException;
import com.claims.processing.claimsservice.exception.PolicyNotActiveException;
import com.claims.processing.claimsservice.exception.PolicyNotFoundException;
import com.claims.processing.claimsservice.exception.ServiceUnavailableException;
import com.claims.processing.claimsservice.repository.ClaimsProcessingReposiory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClaimsProcessingService {

    @Autowired
    private ClaimsProcessingReposiory claimsProcessingReposiory;

    @Autowired
    private ClaimNumberGenerator claimNumberGenerator;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ClaimEventProducer claimEventProducer;

    @Value("${policy.service.host}")
    private String serviceHost;

    @Value("${policy.service.status.url}")
    private String serviceUrl;

    @Transactional
    public ClaimsProcessingResponse addClaims(
            ClaimsProcessingRequest request) {

        PolicyStatusDto policyStatus =
                getPolicyStatus(request.getPolicyNumber());

        if (policyStatus == null ||
                !"Active".equalsIgnoreCase(policyStatus.getStatus())) {
            throw new PolicyNotActiveException(
                    "Policy is not active. Cannot process claims.");
        }

        ClaimsProcessingEntity entity = buildEntity(request);
        claimsProcessingReposiory.save(entity);
        log.info("Publishing event for claim number: {}", entity.getClaimNumber());
        ClaimCreatedEvent event = mapKafkaClaimEvent(entity);
        try {
            claimEventProducer.sendClaimCreatedEvent(event);
        } catch (Exception e) {
            log.error("Failed to send ClaimCreatedEvent for claim number: {}. Error: {}",
                    entity.getClaimNumber(), e.getMessage());
        }

        return mapResponse(entity);
    }

    private ClaimCreatedEvent mapKafkaClaimEvent(ClaimsProcessingEntity entity) {
        ClaimCreatedEvent event = new ClaimCreatedEvent();
        event.setClaimNumber(entity.getClaimNumber());
        event.setPolicyNumber(entity.getPolicyNumber());
        event.setClaimStatus(entity.getClaimStatus());
        event.setClaimDate(entity.getClaimDate());
        event.setClaimAmount(entity.getClaimAmount());
        return event;
    }

    @Transactional(readOnly = true)
    public ClaimsProcessingResponse getClaimsByClaimId(
            String claimNumber) {

        ClaimsProcessingEntity entity = claimsProcessingReposiory
                .findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ClaimNotFoundException(
                        "Claim not found with claim number: " + claimNumber));

        return mapResponse(entity);
    }

    private PolicyStatusDto getPolicyStatus(String policyNumber) {
        try {
            ResponseEntity<PolicyStatusDto> response =
                    restTemplate.getForEntity(
                            serviceHost + serviceUrl + policyNumber,
                            PolicyStatusDto.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new PolicyNotFoundException(
                    "Policy not found: " + policyNumber);
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException(
                    "Policy service unavailable. Try again later.");
        }
    }

    private ClaimsProcessingEntity buildEntity(
            ClaimsProcessingRequest request) {
        ClaimsProcessingEntity entity = new ClaimsProcessingEntity();
        entity.setClaimNumber(claimNumberGenerator.generateclaimNumber());
        entity.setClaimType(request.getClaimType());
        entity.setPolicyNumber(request.getPolicyNumber());
        entity.setClaimAmount(request.getClaimAmount());
        entity.setClaimDate(LocalDate.now());
        entity.setClaimStatus(ClaimStatus.CREATED.name());
        entity.setCustomerName(request.getCustomerName());
        entity.setMobileNumber(request.getMobileNumber());
        return entity;
    }

    private ClaimsProcessingResponse mapResponse(
            ClaimsProcessingEntity entity) {
        ClaimsProcessingResponse response = new ClaimsProcessingResponse();
        response.setClaimNumber(entity.getClaimNumber());
        response.setPolicyNumber(entity.getPolicyNumber());
        response.setClaimType(entity.getClaimType());
        response.setClaimAmount(entity.getClaimAmount());
        response.setClaimStatus(entity.getClaimStatus());
        response.setClaimDate(entity.getClaimDate().toString());
        response.setCustomerName(entity.getCustomerName());
        response.setMobileNumber(entity.getMobileNumber());
        return response;
    }

    @Transactional(readOnly = true)
    public ClaimsPageResponse getAllClaims(int page, int size,
                                           String sortBy, String direction, String status) {

        // Step 1: Build Sort — never null!
        Sort sort = "DESC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<ClaimsProcessingEntity> claimsPage =
                (status != null && !status.isEmpty())
                        ? claimsProcessingReposiory.findByClaimStatus(status, pageRequest)
                        : claimsProcessingReposiory.findAll(pageRequest);

        List<ClaimsProcessingResponse> content = claimsPage.getContent()
                .stream()
                .map(this::mapResponse)
                .collect(Collectors.toList());
        ClaimsPageResponse response = new ClaimsPageResponse();
        response.setContent(content);
        response.setPageNumber(claimsPage.getNumber());
        response.setPageSize(claimsPage.getSize());
        response.setTotalElements(claimsPage.getTotalElements());
        response.setTotalPages(claimsPage.getTotalPages());
        response.setLastPage(claimsPage.isLast());

        return response;
    }
}