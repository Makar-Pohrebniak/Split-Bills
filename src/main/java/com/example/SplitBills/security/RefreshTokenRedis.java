package com.example.SplitBills.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@RedisHash("refresh_tokens")
public class RefreshTokenRedis {

    @Id
    private String token;

    private UUID subId;

    @TimeToLive
    private Long ttl;
}