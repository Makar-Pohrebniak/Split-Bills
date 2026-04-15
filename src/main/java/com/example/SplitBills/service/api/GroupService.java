package com.example.SplitBills.service.api;

import com.example.SplitBills.enums.CurrencyEnum;
import com.example.SplitBills.model.dto.response.GroupResponse;
import java.util.List;
import java.util.UUID;

public interface GroupService {
    GroupResponse createGroup(String groupName, CurrencyEnum currency, UUID subId);

    GroupResponse getGroupById(Long id);

    List<GroupResponse> getGroupsByUserSubId(UUID subId);

    void deleteGroup(Long id, UUID ownerSubId);
}