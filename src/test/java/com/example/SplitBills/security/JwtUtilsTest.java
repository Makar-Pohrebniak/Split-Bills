package com.example.SplitBills.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "mySuperSecretKeyThatIsAtLeast32CharactersLong12345");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000L);
        ReflectionTestUtils.setField(jwtUtils, "jwtRefreshExpirationMs", 7200000L);
    }

    @Test
    void shouldGenerateAndExtractSubIdCorrectly() {
        UUID expectedSubId = UUID.randomUUID();
        List<String> roles = List.of("ROLE_USER");

        String token = jwtUtils.generateToken(expectedSubId, roles);
        UUID actualSubId = jwtUtils.extractSubId(token);

        assertNotNull(token);
        assertEquals(expectedSubId, actualSubId);
    }

    @Test
    void shouldValidateTokenCorrectly() {
        UUID subId = UUID.randomUUID();
        List<String> roles = List.of("ROLE_USER");
        String token = jwtUtils.generateToken(subId, roles);

        boolean isValid = jwtUtils.isTokenValid(token, subId);

        assertTrue(isValid);
    }

    @Test
    void shouldExtractRolesCorrectly() {
        UUID subId = UUID.randomUUID();
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");
        String token = jwtUtils.generateToken(subId, roles);

        List<String> extractedRoles = jwtUtils.extractRoles(token);

        assertNotNull(extractedRoles);
        assertEquals(2, extractedRoles.size());
        assertTrue(extractedRoles.contains("ROLE_USER"));
        assertTrue(extractedRoles.contains("ROLE_ADMIN"));
    }

    @Test
    void shouldGenerateRefreshTokenCorrectly() {
        UUID subId = UUID.randomUUID();

        String refreshToken = jwtUtils.generateRefreshToken(subId);
        UUID actualSubId = jwtUtils.extractSubId(refreshToken);

        assertNotNull(refreshToken);
        assertEquals(subId, actualSubId);
    }

    @Test
    void shouldReturnFalseWhenTokenIsInvalidForDifferentSubId() {
        UUID subId1 = UUID.randomUUID();
        UUID subId2 = UUID.randomUUID();
        String token = jwtUtils.generateToken(subId1, List.of("ROLE_USER"));

        boolean isValid = jwtUtils.isTokenValid(token, subId2);

        assertFalse(isValid);
    }

    @Test
    void shouldCheckTokenExpirationCorrectly() {
        UUID subId = UUID.randomUUID();
        String token = jwtUtils.generateToken(subId, List.of("ROLE_USER"));

        boolean isExpired = jwtUtils.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void shouldThrowExceptionWhenTokenIsExpired() {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000L);
        UUID subId = UUID.randomUUID();
        String token = jwtUtils.generateToken(subId, List.of("ROLE_USER"));

        assertThrows(ExpiredJwtException.class, () -> jwtUtils.isTokenExpired(token));
    }

    @Test
    void shouldReturnCorrectExpirationMs() {
        Long expiration = jwtUtils.getExpirationMs();
        assertEquals(3600000L, expiration);
    }
}