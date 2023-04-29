package ru.ptkom.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ptkom.service.UpdateProducer;
import ru.ptkom.utils.MessageUtils;

import static ru.ptkom.model.KafkaTopic.*;

@Component
public class UpdateController {
    private static final Logger log = Logger.getLogger(UpdateController.class);


    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }
    private TelegramBotController telegramBotController;



    public void registerBot(TelegramBotController telegramBotController) {
        this.telegramBotController = telegramBotController;
    }


    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.getMessage() != null) {
            distributeMessagesByType(update);
        } else if (update.hasCallbackQuery()) {
            distributeMessagesByType(update);
        } else {
            log.error("Unsupported message type: " + update);
        }
    }

    private void distributeMessagesByType(Update update) {
        Message message = update.getMessage();
        if (update.hasCallbackQuery()) {
            processCallbackMessage(update);
            return;
        } else if (message.hasText()) {
            processTextMessage(update);
        } else if (message.hasDocument()) {
            processDocumentMessage(update);
        } else if (message.hasPhoto()) {
            processPictureMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }

    }


    private void setUnsupportedMessageTypeView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update, "Данный тип сообщений не поддерживается");
        setView(sendMessage);
    }

    private void processCallbackMessage(Update update) {
        updateProducer.produce(CALLBACK_MESSAGE_UPDATE, update);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    private void processDocumentMessage(Update update) {
        updateProducer.produce(DOCUMENT_MESSAGE_UPDATE, update);
    }

    private void processPictureMessage(Update update) {
        updateProducer.produce(PICTURE_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    public void setView(SendMessage sendMessage) {
        telegramBotController.sendAnswerMessage(sendMessage);
    }


    private void setFileIsReceivedView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update, "Файл получен");
        setView(sendMessage);
    }
}
