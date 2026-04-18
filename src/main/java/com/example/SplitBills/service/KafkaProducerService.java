package com.example.SplitBills.service;

import com.example.SplitBills.model.dto.FinancialEventDto;
import com.example.SplitBills.model.dto.MemberEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final String MEMBERSHIP_TOPIC = "membership-topic";
    private static final String FINANCIAL_TOPIC = "financial-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMemberEvent(MemberEventDto event) {
        log.info("Sending member event: {}", event);
        kafkaTemplate.send(MEMBERSHIP_TOPIC, event.getGroupId().toString(), event);
    }

    public void sendFinancialEvent(FinancialEventDto event) {
        log.info("Sending financial event: {}", event);
        kafkaTemplate.send(FINANCIAL_TOPIC, event.getGroupId().toString(), event);
    }
}