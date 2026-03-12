package com.bridgepay.payment_processor.config;

import com.bridgepay.payment_processor.model.dto.PaymentCreatedEvent;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@RequiredArgsConstructor
public class SqsConfig {

    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
        SqsMessagingMessageConverter converter = new SqsMessagingMessageConverter();
        converter.setPayloadTypeMapper(message -> PaymentCreatedEvent.class);
        return SqsTemplate.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .messageConverter(converter)
                .build();
    }
}
