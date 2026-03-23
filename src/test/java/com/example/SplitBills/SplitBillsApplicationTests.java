package com.example.SplitBills;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration"
})
class SplitBillsApplicationTests {

	@Test
	void contextLoads() {
	}

}
