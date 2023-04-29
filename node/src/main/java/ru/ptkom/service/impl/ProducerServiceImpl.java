package ru.ptkom.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import ru.ptkom.service.ProducerService;
import ru.ptkom.service.TelegramGraphSender;

import static ru.ptkom.model.KafkaTopic.*;

@Service
public class ProducerServiceImpl implements ProducerService {
    private static final Logger log = Logger.getLogger(ProducerServiceImpl.class);


    private final KafkaTemplate<Long, SendMessage> kafkaSendMessageTemplate;
    private final TelegramGraphSender telegramGraphSender;

    public ProducerServiceImpl(KafkaTemplate<Long, SendMessage> kafkaSendMessageTemplate, ObjectMapper objectMapper, TelegramGraphSender telegramGraphSender) {
        this.kafkaSendMessageTemplate = kafkaSendMessageTemplate;
        this.telegramGraphSender = telegramGraphSender;
    }

    @Override
    public void produceAnswer(SendMessage sendMessage) {
        kafkaSendMessageTemplate.send(ANSWER_SEND_MESSAGE, sendMessage);
    }

    @Override
    public void produceAnswer(SendPhoto sendPhoto) {
        log.debug("Send photo to Kafka: " + sendPhoto);
        telegramGraphSender.sendGraphDirectlyToTelegram(sendPhoto);
    }

}