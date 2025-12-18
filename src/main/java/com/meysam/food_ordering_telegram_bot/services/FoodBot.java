package com.meysam.food_ordering_telegram_bot.services;

import com.meysam.food_ordering_telegram_bot.entities.Food;
import com.meysam.food_ordering_telegram_bot.repositories.FoodRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FoodBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    private final OrderService orderService;
    private final FoodRepository foodRepo;

    private final Map<String, Long> userSelectionState = new ConcurrentHashMap<>();

    public FoodBot(OrderService orderService,  FoodRepository foodRepo) {
        this.orderService = orderService;
        this.foodRepo = foodRepo;
    }

    @Override
    public String getBotUsername() { return botUsername; }

    @Override
    public String getBotToken() { return botToken; }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        }
    }

    private void handleCallback(CallbackQuery callback) {

        String userId = String.valueOf(callback.getFrom().getId());
        String data = callback.getData();

        try {
            Long foodId = Long.parseLong(data);

            userSelectionState.put(userId, foodId);

            sendResponse(userId, "تعداد سفارش خود از این غذا را مشخص کنید");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(Message message) {

        String userId = String.valueOf(message.getFrom().getId());
        String text = message.getText();

        if (text.equals("/start")) {

            orderService.registerUser(String.valueOf(Long.parseLong(userId)));
            sendMenu(userId);
            return;
        }

        if (userSelectionState.containsKey(userId)) {
            processQuantityInput(userId, text);
        } else {
            sendResponse(userId, "برای دیدن منو روی /start کلیک کنید");
        }
    }

    private void processQuantityInput(String userId, String text) {
        try {
            int quantity = Integer.parseInt(text.trim());

            if (quantity <= 0) {
                sendResponse(userId, "لطفا یک عدد وارد کنید. عدد باید بزرگتر از صفر و با بصورت اعداد انگلیسی وارد شود");
                return;
            }

            Long foodId = userSelectionState.get(userId);

            orderService.createOrder(String.valueOf(Long.parseLong(userId)), foodId, quantity);

            userSelectionState.remove(userId);
            Double chosenFoodPrice = foodRepo.findById(foodId).map(Food::getPrice).orElse(0.0);

            sendResponse(userId, "سفارش شما تکمیل شد بعد از دریافت سفارش مبلغ: " + quantity * chosenFoodPrice + " تومان به مامور ارسال غذا تحویل دهید");

        } catch (NumberFormatException e) {
            sendResponse(userId, "لطفا یک عدد وارد کنید. عدد باید بزرگتر از صفر و با بصورت اعداد انگلیسی وارد شود");
        }
    }

    private void sendMenu(String chatId) {
        List<Food> foods = orderService.getAllFood();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Food food : foods) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(food.getName() + " - " + food.getPrice() + " تومان ");
            button.setCallbackData(String.valueOf(food.getId()));

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            rows.add(row);
        }
        markup.setKeyboard(rows);

        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text("منو:")
                    .replyMarkup(markup)
                    .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(String chatId, String text) {
        try {
            execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}