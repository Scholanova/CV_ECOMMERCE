package com.scholanova.ecommerce.order.controller;

import com.scholanova.ecommerce.order.repository.OrderRepository;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableSpringDataWebSupport
@RequestMapping("/orders")
public class OrderController {

    private OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
