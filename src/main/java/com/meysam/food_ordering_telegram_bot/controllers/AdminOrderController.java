package com.meysam.food_ordering_telegram_bot.controllers;

import com.meysam.food_ordering_telegram_bot.entities.Order;
import com.meysam.food_ordering_telegram_bot.repositories.OrderRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderRepository orderRepository;

    public AdminOrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<Order> getAllOrders(@RequestParam(required = false) Long userId) {
        if (userId != null) {

            return orderRepository.findAll().stream()
                    .filter(order -> order.getUser().getTelegramId().equals(userId))
                    .collect(Collectors.toList());
        }

        return orderRepository.findAll();

    }
}
