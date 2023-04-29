package ru.ptkom.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ptkom.service.UpdateProducer;

@Service
public class UpdateProducerImpl implements UpdateProducer {
    private static final Logger log = Logger.getLogger(UpdateProducerImpl.class);
    private final KafkaTemplate<Long, Update> kafkaUpdateTemplate;
    private final ObjectMapper objectMapper;

    public UpdateProducerImpl(KafkaTemplate<Long, Update> kafkaUpdateTemplate, ObjectMapper objectMapper) {
        this.kafkaUpdateTemplate = kafkaUpdateTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void produce(String kafkaTopic, Update update) {
        kafkaUpdateTemplate.send(kafkaTopic, update);
        if (update.hasMessage()) {
            log.debug("Message without callback data produced to Kafka topic: " + kafkaTopic + "; Message: " + update.getMessage());
        } else if (update.hasCallbackQuery()) {
            log.debug("Message with callback data produced to Kafka topic: "  + kafkaTopic + "; Message: " + update.getCallbackQuery());
        }

    }
}
