package com.example.SplitBills.service.api;

import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface GroupService {
    GroupEntity createGroup(String name, UserEntity owner);

    GroupEntity getGroupById(Long id);

    List<GroupEntity> getGroupsByUserSubId(String subId);
}
