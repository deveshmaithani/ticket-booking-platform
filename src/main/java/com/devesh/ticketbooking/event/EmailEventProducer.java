package com.devesh.ticketbooking.event;

import com.devesh.ticketbooking.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishEmailConfirmation(EmailNotificationEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                event
        );
    }
}