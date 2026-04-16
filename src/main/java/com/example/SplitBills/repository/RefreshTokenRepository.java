package com.example.SplitBills.repository;

import com.example.SplitBills.security.RefreshTokenRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshTokenRedis, String> {
}
