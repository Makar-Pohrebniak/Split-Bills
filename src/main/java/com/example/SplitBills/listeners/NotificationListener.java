package com.example.SplitBills.listeners;

import com.example.SplitBills.model.dto.FinancialEventDto;
import com.example.SplitBills.model.dto.MemberEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationListener {

    @KafkaListener(
            topics = "membership-topic",
            groupId = "splitbills-notification-group"
    )
    public void logMemberEvent(MemberEventDto event) {
        log.info("NOTIFICATION: User {} was {} group {}",
                event.getUserId(), event.getAction(), event.getGroupId());
    }

    @KafkaListener(
            topics = "financial-topic",
            groupId = "splitbills-notification-group"
    )
    public void logFinancialEvent(FinancialEventDto event) {
        log.info("NOTIFICATION: Financial event in group {}: {} (Amount: {})",
                event.getGroupId(), event.getMessage(), event.getAmount());
    }
}