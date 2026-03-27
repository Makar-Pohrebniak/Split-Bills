package com.example.SplitBills.repository;

import com.example.SplitBills.model.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Integer> {
    @Query("SELECT g FROM GroupEntity g WHERE g.id = :id")
    Optional<GroupEntity> getGroupById(@Param("id") Long id);

    @Query("SELECT g FROM GroupEntity g JOIN g.members m WHERE m.subId = :subId")
    List<GroupEntity> findAllByMemberSubId(@Param("subId") UUID subId);

}
