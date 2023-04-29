package ru.ptkom.service.impl;

import org.apache.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.ptkom.service.ZabbixAlertProducer;

import javax.annotation.PostConstruct;

import static ru.ptkom.model.KafkaTopic.ZABBIX_ALERTS;

@Service
public class ZabbixAlertProducerImpl implements ZabbixAlertProducer {
    private static final Logger log = Logger.getLogger(ZabbixAlertProducerImpl.class);

    private final KafkaTemplate<Long, String> kafkaStringTemplate;

    public ZabbixAlertProducerImpl(KafkaTemplate<Long, String> kafkaStringTemplate) {
        this.kafkaStringTemplate = kafkaStringTemplate;
    }


    @Override
    public void produce(String alert) {
        kafkaStringTemplate.send(ZABBIX_ALERTS, alert);
    }

    @PostConstruct
    private void test() {
        produce("{\"type\": \"problemSpoted\",\"name\": \"{EVENT.NAME}\",\"time\": \"{EVENT.TIME}\",\"date\": \"{EVENT.DATE}\",\"host\": \"{HOST.NAME}\",\"severity\": \"{EVENT.SEVERITY}\",\"data\": \"{EVENT.OPDATA}\",\"id\": \"5342806\",\"url\": \"{TRIGGER.URL}\",\"hostId\": \"10640\"}");
    }
}
