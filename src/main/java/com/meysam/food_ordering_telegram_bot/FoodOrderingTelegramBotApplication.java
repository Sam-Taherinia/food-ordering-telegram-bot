package com.meysam.food_ordering_telegram_bot;

import com.meysam.food_ordering_telegram_bot.entities.Food;
import com.meysam.food_ordering_telegram_bot.entities.Order;
import com.meysam.food_ordering_telegram_bot.entities.User;
import com.meysam.food_ordering_telegram_bot.repositories.FoodRepository;
import com.meysam.food_ordering_telegram_bot.repositories.OrderRepository;
import com.meysam.food_ordering_telegram_bot.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;

@SpringBootApplication
public class FoodOrderingTelegramBotApplication {

	public static void main(String[] args) {

        SpringApplication.run(FoodOrderingTelegramBotApplication.class, args);

	}

    @Bean
    @Profile("!test")
    CommandLineRunner run(FoodRepository foodRepo, UserRepository userRepo, OrderRepository orderRepo) {
        return args -> {

            if (foodRepo.count() == 0) {
                Food pizza = new Food(null, "پیتزا پپرونی", 345_000.0);
                Food burger = new Food(null, "چلو جوجه", 125_000.0);
                foodRepo.saveAll(List.of(pizza, burger));
            }

            if (userRepo.count() == 0) {
                User user1 = new User("@MeysamTN");
                User user2 = new User("@Ali");
                userRepo.saveAll(List.of(user1, user2));
            }

            if (orderRepo.count() == 0) {

                List<Food> foods = foodRepo.findAll();
                List<User> users = userRepo.findAll();

                if (!foods.isEmpty() && !users.isEmpty()) {

                    Order o1 = new Order();
                    o1.setUser(users.get(0));
                    o1.setFood(foods.get(0));
                    o1.setQuantity(2);

                    Order o2 = new Order();
                    o2.setUser(users.get(1));
                    o2.setFood(foods.get(1));
                    o2.setQuantity(1);

                    orderRepo.saveAll(List.of(o1, o2));
                }
                orderRepo.flush();
            }
        };
    }

}
