package com.example.SplitBills.listeners;

import com.example.SplitBills.model.dto.MemberEventDto;
import com.example.SplitBills.service.api.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecalculationListener {

    private final ExpenseService expenseService;

    @KafkaListener(
            topics = "membership-topic",
            groupId = "splitbills-recalculation-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleMemberChange(MemberEventDto event) {
        log.info("--- ATTEMPTING RECALCULATION FOR GROUP: {} ---", event.getGroupId());

        try {
            expenseService.recalculateGroupExpenses(event.getGroupId());
            log.info("--- RECALCULATION SUCCESSFUL ---");
        } catch (Exception e) {
            log.error("--- RECALCULATION FAILED: {} ---", e.getMessage());
        }
    }
}
