package org.example.service.service;


import lombok.RequiredArgsConstructor;
import org.example.service.event.OrderSendEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMessagingService {

    @Value("${topic.send-order}")
    private String sendClientTopic;

    private final KafkaTemplate<String , Object> kafkaTemplate;

    public void sendOrder(OrderSendEvent orderSendEvent) {
        kafkaTemplate.send(sendClientTopic, orderSendEvent.getBarCode(), orderSendEvent);
    }

}