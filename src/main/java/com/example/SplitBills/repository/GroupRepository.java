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
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    Optional<GroupEntity> findById(Long id);

    @Query("SELECT g FROM GroupEntity g JOIN g.members m WHERE m.subId = :subId")
    List<GroupEntity> findAllByMembersSubId(@Param("subId") UUID subId);

    @Query("SELECT COUNT(g) > 0 FROM GroupEntity g JOIN g.members m WHERE g.id = :groupId AND m.id = :userId")
    boolean existsByGroupIdAndMemberId(@Param("groupId") Long groupId, @Param("userId") Long userId);

}