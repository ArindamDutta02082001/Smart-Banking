package com.royal.reserve.bank.asset.management.api.config.Kafka.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name("user.notify")
                .partitions(1)
                .replicas(1)
                .build();
    }

}

//to see the events inside a kafka topic

// docker exec -it notification-api-kafka bash

// kafka-console-consumer --bootstrap-server notification-api-kafka:29092 --topic user.notify --from-beginning
