package com.example.project.controller;

import com.example.project.service.DataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final DataService dataService;
    private int rowNumber = 1; // Номер строки для хранения данных

    public MyTelegramBot(DataService dataService) {
        this.dataService = dataService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
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
                sendMessage(chatId, "Привет! Введите значения, которые хотите сохранить в БД.");
            } else {
                saveValue(chatId, text);
            }
        }
    }

    private void saveValue(String chatId, String value) {
        dataService.saveData(value, rowNumber++);
        sendMessage(chatId, "Значение '" + value + "' сохранено!");
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

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
