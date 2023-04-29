package ru.ptkom.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ptkom.service.TelegramGraphSender;

@Service
public class TelegramGraphSenderImpl extends TelegramWebhookBot implements TelegramGraphSender {
    private static final Logger log = Logger.getLogger(TelegramGraphSenderImpl.class);


    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;



    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }



    public void sendGraphDirectlyToTelegram(SendPhoto photo) {
        if (photo != null) {
            try {
                execute(photo);
            } catch (TelegramApiException e) {
                log.error("Unable to send graph: " + e + "; Message: " + photo);
            }
        }
    }




    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return null;
    }
}