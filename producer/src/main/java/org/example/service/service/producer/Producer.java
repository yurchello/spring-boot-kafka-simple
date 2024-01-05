package org.example.service.service.producer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Order;
import org.example.service.event.OrderSendEvent;
import org.example.service.service.KafkaMessagingService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Producer {

    private final KafkaMessagingService kafkaMessagingService;
    private final ModelMapper modelMapper;

    public Order sendOrderEvent(Order order) {
        kafkaMessagingService.sendOrder(modelMapper.map(order, OrderSendEvent.class));
        log.info("Send order from producer {}", order);
        return order;
    }
}