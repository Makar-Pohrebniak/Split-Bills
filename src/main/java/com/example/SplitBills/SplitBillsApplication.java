package com.example.SplitBills;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class SplitBillsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SplitBillsApplication.class, args);
	}

}
