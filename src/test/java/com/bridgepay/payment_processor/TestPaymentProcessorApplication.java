package com.bridgepay.payment_processor;

import org.springframework.boot.SpringApplication;

public class TestPaymentProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.from(PaymentProcessorApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
