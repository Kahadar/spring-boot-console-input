package com.example.project.controller;

import com.example.project.service.DataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyTelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final DataService dataService;
    private final ApplicationContext context; // Контекст для завершения Spring Boot

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            log.info("Бот успешно запущен!");

            // Отправка приветствия в лог
            String greetingMessage = loadGreetingMessage();
            log.info("Приветственное сообщение: \n{}", greetingMessage);

        } catch (TelegramApiException e) {
            log.error("Ошибка при регистрации бота", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String text = message.getText().trim();
            String chatId = message.getChatId().toString();
            Long userId = message.getFrom().getId();

            if (text.startsWith("/exist")) {
                handleExistCommand(chatId, userId, text);
            } else {
                switch (text) {
                    case "/start":
                        sendMessage(chatId, loadGreetingMessage());
                        break;
//
//                    case "/sum":
//                        handleSumCommand(chatId);
//                        break;

                    case "/shutdown":
                        handleShutdownCommand(chatId);
                        break;

                    default:
                        handleInput(chatId, userId, text);
                }
            }
        }
    }

    private void handleInput(String chatId, Long userId, String text) {
        try {
            int cost = Integer.parseInt(text);
            dataService.saveData(userId, cost);
            sendMessage(chatId, "Значение '" + cost + "' сохранено!");
            handleMySumCommand(chatId, userId); // Отправляем текущую сумму
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Ошибка: Введите число!");
        }
    }

//    private void handleSumCommand(String chatId) {
//        int totalSum = dataService.getTotalSum();
//        double portion = totalSum / 3.0;
//        sendMessage(chatId, "Сумма: " + totalSum + "\nТвой кусок: " + portion);
//    }

    private void handleMySumCommand(String chatId, Long userId) {
        int userSum = dataService.getUserSum(userId);
        double portion = userSum / 3.0;
        sendMessage(chatId, "Сумма по заказам: " + userSum + "\nТвой кусок: " + portion);
    }

    private void handleExistCommand(String chatId, Long userId, String text) {
        String[] parts = text.split(" ");
        if (parts.length != 2) {
            sendMessage(chatId, "Ошибка: Введите число после /exist, например: /exist 100");
            return;
        }

        try {
            int value = Integer.parseInt(parts[1]);
            int cost = value * 3;
            dataService.saveData(userId, cost);
            sendMessage(chatId, "Зп " + value + " добавлена в расчетный период");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Ошибка: Введите корректное число после /exist.");
        }
    }

    private void handleShutdownCommand(String chatId) {
        sendMessage(chatId, "Бот завершает работу...");
        log.info("Бот завершает работу по команде /shutdown");

        Thread shutdownThread = new Thread(() -> {
            try {
                Thread.sleep(1000); // Даем время на отправку сообщения
            } catch (InterruptedException ignored) {}

            SpringApplication.exit(context, () -> 0);
            System.exit(0);
        });
        shutdownThread.start();
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения", e);
        }
    }

    private String loadGreetingMessage() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("greeting.txt").getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().reduce("", (acc, line) -> acc + line + "\n").trim();
        } catch (IOException e) {
            log.error("Ошибка при загрузке приветственного сообщения", e);
            return "Привет! Введите число для сохранения.";
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
