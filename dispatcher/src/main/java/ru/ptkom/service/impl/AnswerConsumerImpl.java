package ru.ptkom.service.impl;

import org.apache.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import ru.ptkom.controller.UpdateController;
import ru.ptkom.service.AnswerConsumer;

import static ru.ptkom.model.KafkaTopic.ANSWER_SEND_MESSAGE;
import static ru.ptkom.model.KafkaTopic.ANSWER_SEND_PHOTO;


@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private static final Logger log = Logger.getLogger(AnswerConsumerImpl.class);

    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @KafkaListener(id = "AnswerMessageConsumer", topics = ANSWER_SEND_MESSAGE, containerFactory = "singleSendMessageFactory")
    public void consumeMessage(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }

}
