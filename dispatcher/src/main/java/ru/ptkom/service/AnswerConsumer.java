package ru.ptkom.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public interface AnswerConsumer {

    void consumeMessage(SendMessage sendMessage);
}
