package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderDto;
import org.example.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class TestController {

    private final OrderService orderService;

    @PostMapping
    public String sendOrder() {
        OrderDto clientDto = new OrderDto();
        clientDto.setBarCode("bar1");
        clientDto.setPrice(new BigDecimal(1));
        clientDto.setQuantity(2);
        clientDto.setProductName("prod");
        orderService.save(clientDto);
        return "";
    }
}