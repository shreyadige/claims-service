package com.claims.processing.claimsservice.events;

import com.claims.processing.kafka.common.ClaimCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClaimCreatedEventProducer {

    @Autowired
    private KafkaTemplate<String, ClaimCreatedEvent> kafkaTemplate;

    @Value("${kafka.topic.claim-created}")
    private String claimCreatedTopic;

    public void sendClaimCreatedEvent(ClaimCreatedEvent event) {
        kafkaTemplate.send(claimCreatedTopic, event.getClaimNumber(), event);
        log.info("Sent ClaimCreatedEvent for claim number: {}", event.getClaimNumber());

    }

}
