package org.example.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class KafkaMessagingService {
    private static final String topicCreateOrder = "${topic.send-order}";
    private static final String kafkaConsumerGroupId = "${spring.kafka.consumer.group-id}";


    @KafkaListener(
            topics = topicCreateOrder,
            groupId = kafkaConsumerGroupId,
            properties = {"spring.json.value.default.type=org.example.service.OrderEvent"})
//    @RetryableTopic(kafkaTemplate = "kafkaTemplate",
//            attempts = "4",
//            backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 1500)
//    )
    public OrderEvent printOrder(OrderEvent orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Message consumed (CLI consumer) {}", orderEvent);
        return orderEvent;
    }

}
