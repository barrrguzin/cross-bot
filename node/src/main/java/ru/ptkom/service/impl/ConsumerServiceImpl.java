package ru.ptkom.service.impl;

import org.apache.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ptkom.service.AlertService;
import ru.ptkom.service.ConsumerService;
import ru.ptkom.service.MainService;


import static ru.ptkom.model.KafkaTopic.*;

@Service
public class ConsumerServiceImpl implements ConsumerService {
    private static final Logger log = Logger.getLogger(ConsumerServiceImpl.class);

    private final MainService mainService;
    private final AlertService alertService;

    public ConsumerServiceImpl(MainService mainService, AlertService alertService) {
        this.mainService = mainService;
        this.alertService = alertService;
    }


    @Override
    @KafkaListener(id = "TextMessageConsumer", topics = TEXT_MESSAGE_UPDATE, containerFactory = "singleUpdateFactory")
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: Text message received: " + update.getMessage().getText());
        mainService.processTextMessage(update);
    }

    @Override
    @KafkaListener(id = "DocumentMessageConsumer", topics = DOCUMENT_MESSAGE_UPDATE, containerFactory = "singleUpdateFactory")
    public void consumeDocumentMessageUpdates(Update update) {
        log.debug("NODE: Document message received");
    }

    @Override
    @KafkaListener(id = "PictureMessageConsumer", topics = PICTURE_MESSAGE_UPDATE, containerFactory = "singleUpdateFactory")
    public void consumePictureMessageUpdates(Update update) {
        log.debug("NODE: Picture message received");
    }

    @Override
    @KafkaListener(id = "CallbackMessageConsumer", topics = CALLBACK_MESSAGE_UPDATE, containerFactory = "singleUpdateFactory")
    public void consumeCallbackUpdates(Update update) {
        log.debug("NODE: Callback message received: " + update.getCallbackQuery().getData());
        mainService.processCallbackMessage(update);
    }

    @Override
    @KafkaListener(id = "ZabbixAlertConsumer", topics = ZABBIX_ALERTS, containerFactory = "singleStringFactory")
    public void consumeZabbixAlert(String string) {
        log.debug("NODE: Zabbix alert received: " + string);
        alertService.processAlert(string);
    }
}
