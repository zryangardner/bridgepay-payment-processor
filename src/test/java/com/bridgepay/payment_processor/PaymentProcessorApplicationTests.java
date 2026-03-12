package com.bridgepay.payment_processor;

import com.bridgepay.payment_processor.messaging.SqsPublisher;
import io.awspring.cloud.autoconfigure.sqs.SqsAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@SpringBootTest
@EnableAutoConfiguration(exclude = {SqsAutoConfiguration.class})
class PaymentProcessorApplicationTests {

	@MockitoBean
	private SqsPublisher sqsPublisher;

	@MockitoBean
	private SqsAsyncClient sqsAsyncClient;

	@Test
	void contextLoads() {
	}
}
