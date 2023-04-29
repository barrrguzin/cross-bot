package ru.ptkom.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducer {

    void produce(String kafkaQueue, Update update);
}
