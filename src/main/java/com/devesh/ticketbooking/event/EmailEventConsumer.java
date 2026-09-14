package com.devesh.ticketbooking.event;

import com.devesh.ticketbooking.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailEventConsumer {

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailConfirmation(EmailNotificationEvent event) {
        log.info("📧 [RabbitMQ Consumer] Attempting to send confirmation email to {} for booking {}",
                event.getUserEmail(), event.getBookingId());

        // TEMPORARY: simulate a failing email service for testing DLQ behavior
//        if (event.getUserEmail().contains("failtest")) {
//            log.warn("⚠️ [RabbitMQ Consumer] Simulated failure for {}", event.getUserEmail());
//            throw new RuntimeException("Simulated email service failure");
//        }

        log.info("✅ [RabbitMQ Consumer] Email sent successfully: 'Hi {}, your seat for event {} is confirmed!'",
                event.getUserName(), event.getEventId());
    }
}