package com.meysam.food_ordering_telegram_bot.repositories;

import com.meysam.food_ordering_telegram_bot.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
