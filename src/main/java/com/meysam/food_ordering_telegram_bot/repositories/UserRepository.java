package com.meysam.food_ordering_telegram_bot.repositories;

import com.meysam.food_ordering_telegram_bot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
