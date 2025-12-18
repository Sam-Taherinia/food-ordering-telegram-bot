package com.meysam.food_ordering_telegram_bot.services;

import com.meysam.food_ordering_telegram_bot.entities.Food;
import com.meysam.food_ordering_telegram_bot.entities.Order;
import com.meysam.food_ordering_telegram_bot.entities.User;
import com.meysam.food_ordering_telegram_bot.repositories.FoodRepository;
import com.meysam.food_ordering_telegram_bot.repositories.OrderRepository;
import com.meysam.food_ordering_telegram_bot.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final FoodRepository foodRepo;

    public OrderService(OrderRepository orderRepo, UserRepository userRepo, FoodRepository foodRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.foodRepo = foodRepo;
    }

    public User registerUser(String telegramId) {
        return userRepo.findById((telegramId))
                .orElseGet(() -> userRepo.save(new User(telegramId)));
    }

    public void createOrder(String telegramId, Long foodId, int quantity) {
        User user = registerUser(telegramId);
        Food food = foodRepo.findById(foodId).orElseThrow(() -> new RuntimeException("Food not found"));

        Order order = new Order();
        order.setUser(user);
        order.setFood(food);
        order.setQuantity(quantity);

        orderRepo.save(order);
    }

    public List<Food> getAllFood() {
        return foodRepo.findAll();
    }
}