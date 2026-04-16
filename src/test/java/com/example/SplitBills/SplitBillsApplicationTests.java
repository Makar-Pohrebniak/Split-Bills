package com.example.SplitBills;

import com.example.SplitBills.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class SplitBillsApplicationTests {

	@MockitoBean
	private RefreshTokenRepository refreshTokenRepository;

	@Test
	void contextLoads() {
	}
}