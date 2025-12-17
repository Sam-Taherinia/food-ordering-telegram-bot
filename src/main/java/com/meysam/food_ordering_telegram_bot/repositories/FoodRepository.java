package com.meysam.food_ordering_telegram_bot.repositories;

import com.meysam.food_ordering_telegram_bot.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, String> {
}
