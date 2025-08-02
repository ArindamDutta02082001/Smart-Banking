package com.royal.reserve.bank.notification.api;

import com.royal.reserve.bank.notification.api.event.TransactionEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * Main class for the Notification Api.
 */
@SpringBootApplication
@Slf4j
public class NotificationApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApiApplication.class, args);
	}


}
