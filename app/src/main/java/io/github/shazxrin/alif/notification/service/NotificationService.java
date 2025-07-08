package io.github.shazxrin.alif.notification.service;

import io.github.shazxrin.notifier.common.NotificationMessage;
import io.github.shazxrin.notifier.common.NotificationMessageQueue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final static String APP_NAME = "alif";
    private final static String ROUTING_KEY = "";

    private final RabbitTemplate rabbitTemplate;

    public NotificationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(String title, String message) {
        NotificationMessage notificationMessage = new NotificationMessage(
            APP_NAME,
            title,
            message
        );

        rabbitTemplate.convertAndSend(NotificationMessageQueue.EXCHANGE_NAME, ROUTING_KEY, notificationMessage);
    }
}
