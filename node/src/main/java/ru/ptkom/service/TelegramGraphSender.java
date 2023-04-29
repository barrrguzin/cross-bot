package ru.ptkom.service;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public interface TelegramGraphSender {
    void sendGraphDirectlyToTelegram(SendPhoto sendPhoto);
}
