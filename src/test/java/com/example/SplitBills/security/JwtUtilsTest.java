package com.example.SplitBills.security;

import com.example.SplitBills.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "mySuperSecretKeyThatIsAtLeast32CharactersLong12345");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000L);
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
}