package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Order;
import org.example.service.service.producer.Producer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final Producer producer;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Order sendOrder(@RequestBody Order order) {
        log.info("Send order to kafka");
        producer.sendOrderEvent(order);
        return order;
    }
}
