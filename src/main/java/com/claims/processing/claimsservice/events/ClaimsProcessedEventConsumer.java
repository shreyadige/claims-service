package com.claims.processing.claimsservice.events;

import com.claims.processing.claimsservice.entity.ClaimsProcessingEntity;
import com.claims.processing.claimsservice.repository.ClaimsProcessingReposiory;
import com.claims.processing.kafka.common.ClaimProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;

@Service
@Slf4j
public class ClaimsProcessedEventConsumer {

    @Autowired
    private ClaimsProcessingReposiory claimsProcessingReposiory;

    @Transactional
    @KafkaListener(topics = "${kafka.topic.claim-processed}", groupId = "${kafka.consumer.group-id}")
    public void consume(ClaimProcessedEvent event, Acknowledgment acknowledgment) {
        // For demonstration, we just log the event. In a real application, you might update claim status, etc.
        if (event.getClaimNumber() == null || event.getClaimNumber().isEmpty() || event.getStatus() == null || event.getStatus().isEmpty()) {
            log.warn("Received ClaimProcessedEvent with missing claim number. Skipping.");
            acknowledgment.acknowledge();
            return;
        }
        try {
            Optional<ClaimsProcessingEntity> claimNumber = claimsProcessingReposiory.findByClaimNumber(event.getClaimNumber());
            if (claimNumber.isEmpty()) {
                log.info("Claim is not found for claim number : {}. Event is skipped.", event.getClaimNumber());
                acknowledgment.acknowledge();
                return;
            }
            ClaimsProcessingEntity claim = claimNumber.get();
            if (claim.getClaimStatus().equalsIgnoreCase(event.getStatus())) {
                log.info("Claim {} already has status {}. Skipping update.", event.getClaimNumber(), event.getStatus());
                acknowledgment.acknowledge();
                return;
            }
            claim.setClaimStatus(event.getStatus());
            claim.setClaimDate(event.getProcessedDate().toLocalDate());
            claimsProcessingReposiory.save(claim);

            log.info("Successfully updated claim {} to status {}",
                    event.getClaimNumber(), event.getStatus());
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        public void afterCommit() {
                            acknowledgment.acknowledge();
                        }
                    }
            );


        } catch (Exception e) {
            log.error("Error updating claim {}: {}", event.getClaimNumber(), e.getMessage());
            throw e; // Let Spring retry the message
        }
    }
}
