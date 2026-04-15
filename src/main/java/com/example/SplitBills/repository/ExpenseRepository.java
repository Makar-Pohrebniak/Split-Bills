package com.example.SplitBills.repository;

import com.example.SplitBills.model.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity,Long> {

    @Query("SELECT e FROM ExpenseEntity e JOIN FETCH e.payer WHERE e.group.id = :groupId ORDER BY e.createdAt DESC")
    List<ExpenseEntity> findByGroupId(@Param("groupId") Long groupId);
}
