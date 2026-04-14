package com.example.SplitBills.repository;

import com.example.SplitBills.model.entity.ExpenseEntity;
import com.example.SplitBills.model.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity,Long> {
    Optional<ExpenseEntity> findByGroupId(Long groupId);
}
