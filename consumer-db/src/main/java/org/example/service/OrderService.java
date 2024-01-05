package org.example.service;


import org.example.dto.OrderDto;
import org.example.entity.Order;

public interface OrderService {
    Order save(OrderDto clientDto);
}
