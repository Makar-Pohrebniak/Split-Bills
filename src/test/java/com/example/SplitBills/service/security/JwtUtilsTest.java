package com.example.SplitBills.service.security;

import com.example.SplitBills.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "my-super-secret-key-32-characters-long-for-test");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000L);
    }

    @Test
    void shouldGenerateAndExtractSubIdCorrectly() {
        UUID expectedSubId = UUID.randomUUID();

        String token = jwtUtils.generateToken(expectedSubId);
        UUID actualSubId = jwtUtils.extractSubId(token);

        assertNotNull(token);
        assertEquals(expectedSubId, actualSubId);
    }
}