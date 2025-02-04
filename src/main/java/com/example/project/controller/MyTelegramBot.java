package com.example.project.controller;

import com.example.project.service.DataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
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

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            log.info("Бот успешно запущен!");

            // Отправка приветствия
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

            if (text.equalsIgnoreCase("/start")) {
                sendMessage(chatId, loadGreetingMessage());
            } else {
                handleInput(chatId, text);
            }
        }
    }

    private void handleInput(String chatId, String text) {
        try {
            int cost = Integer.parseInt(text);
            dataService.saveData(cost);
            sendMessage(chatId, "Значение '" + cost + "' сохранено!");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Ошибка: Введите число!");
        }
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
