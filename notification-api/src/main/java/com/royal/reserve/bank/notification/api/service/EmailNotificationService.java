package com.royal.reserve.bank.notification.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.royal.reserve.bank.notification.api.event.NotifyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     *This method is a Kafka message listener for the "notificationTopic" topic.
     *It handles incoming messages and processes the NotifyEvent object.
     *@param notifyEvent The NotifyEvent object received from the Kafka message.
     */

    @KafkaListener(topics = "user.notify", groupId = "asset-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleTransactionEvent(String notifyEvent) throws JsonProcessingException {
        log.info("Received NotifyEvent: {}", notifyEvent);


        ObjectMapper mapper = new ObjectMapper();
        NotifyEvent event = mapper.readValue(notifyEvent, NotifyEvent.class);

        String subject = "SmartBank Transaction Notification";

        String senderMsg = String.format(
                "Hi %s,\n\nYou have sent "+event.getCurrency()+" %d to %s (%s).\nMessage: %s\n\nThank you,\nSmartBank",
                event.getSenderName(), event.getAmt(), event.getReceiverName(), event.getReceiverMob(), event.getMessage()
        );

        String receiverMsg = String.format(
                "Hi %s,\n\nYou have received  "+event.getCurrency()+" %d from %s (%s).\nMessage: %s\n\nThank you,\nSmartBank",
                event.getReceiverName(), event.getAmt(), event.getSenderName(), event.getSenderMob(), event.getMessage()
        );

        // Replace this with real lookup in future
        String senderEmail = event.getSenderMail();
        String receiverEmail = event.getReceiverMail();

        sendEmail(senderEmail, subject, senderMsg);
        sendEmail(receiverEmail, subject, receiverMsg);
    }

    private void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("duttaarindam902@gmail.com");
        mailSender.send(message);
        log.info("Email sent to {}", toEmail);
    }

}

