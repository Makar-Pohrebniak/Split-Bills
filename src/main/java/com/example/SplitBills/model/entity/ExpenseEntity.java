package com.example.SplitBills.model.entity;

import com.example.SplitBills.model.dto.response.GroupResponse;
import com.example.SplitBills.model.dto.response.UserResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "expenses")
public class ExpenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id")
    private UserEntity payer;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
    private List<ExpenseShare> shares;

    private LocalDateTime createdAt;
}
