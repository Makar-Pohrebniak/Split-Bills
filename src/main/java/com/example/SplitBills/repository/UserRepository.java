package com.example.SplitBills.repository;

import com.example.SplitBills.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findBySubId(UUID subId);

    List<UserEntity> findByUsername(String username);

}
