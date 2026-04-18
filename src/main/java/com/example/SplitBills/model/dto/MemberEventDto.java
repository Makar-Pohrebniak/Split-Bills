package com.example.SplitBills.model.dto;

import com.example.SplitBills.enums.MemberAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberEventDto {
    private Long groupId;
    private Long userId;
    private MemberAction action;
}