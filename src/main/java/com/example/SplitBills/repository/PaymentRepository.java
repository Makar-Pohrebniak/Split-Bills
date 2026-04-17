package com.example.SplitBills.repository;

import com.example.SplitBills.model.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    List<PaymentEntity> findAllByGroupId(Long groupId);

    @Query("SELECT p FROM PaymentEntity p WHERE p.groupId = :groupId AND p.status = 'CONFIRMED'")
    List<PaymentEntity> findAllConfirmedByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT p FROM PaymentEntity p WHERE (p.senderId = :userId OR p.receiverId = :userId) AND p.groupId = :groupId")
    List<PaymentEntity> findAllUserPaymentsInGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);
}