package com.claims.processing.claimsservice.events;

import com.claims.processing.claimsservice.entity.ClaimsProcessingEntity;
import com.claims.processing.claimsservice.repository.ClaimsProcessingReposiory;
import com.claims.processing.kafka.common.ClaimProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ClaimsProcessedEventConsumer {

    @Autowired
    private ClaimsProcessingReposiory claimsProcessingReposiory;

    @Transactional
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 5000),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = "${kafka.topic.claim-processed}",
            groupId = "${kafka.consumer.group-id}"
    )
    public void consume(ClaimProcessedEvent event) {

        if (event.getClaimNumber() == null ||
                event.getClaimNumber().isEmpty() ||
                event.getStatus() == null ||
                event.getStatus().isEmpty()) {

            log.warn("Invalid claim event received");
            return;
        }

        ClaimsProcessingEntity claim = claimsProcessingReposiory
                .findByClaimNumber(event.getClaimNumber())
                .orElseThrow(() ->
                        new RuntimeException("Claim not found"));

        if (claim.getClaimStatus()
                .equalsIgnoreCase(event.getStatus())) {

            log.info("Claim {} already updated with status {}",
                    event.getClaimNumber(),
                    event.getStatus());

            return;
        }

        claim.setClaimStatus(event.getStatus());
        claim.setClaimDate(event.getProcessedDate().toLocalDate());

        claimsProcessingReposiory.save(claim);

        log.info("Successfully updated claim {} to status {}",
                event.getClaimNumber(),
                event.getStatus());
    }

    @DltHandler
    public void dltHandler(ClaimProcessedEvent event) {

        log.error("Message moved to DLT for claim {}",
                event.getClaimNumber());

    }
}