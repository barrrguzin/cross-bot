package ru.ptkom.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.ptkom.model.Alert;
import ru.ptkom.service.AlertService;
import ru.ptkom.service.ProducerService;
import ru.ptkom.service.ViewService;

@Service
public class AlertServiceImpl implements AlertService {
    private static final Logger log = Logger.getLogger(AlertServiceImpl.class);
    private final ProducerService producerService;
    private final ViewService viewService;

    private final ObjectMapper mapper = new ObjectMapper();


    public AlertServiceImpl(ProducerService producerService, ViewService viewService) {
        this.producerService = producerService;
        this.viewService = viewService;
    }




    @Override
    public void processAlert(String alertFromZabbix) {

        try {

            Alert alert = mapper.readValue(alertFromZabbix, Alert.class);

            if (alert.getType().equals("problemSpoted")){
                sendProblemView(alert);
            } else if (alert.getType().equals("problemResolved")){

            } else if (alert.getType().equals("problemUpdate")){

            } else if (alert.getType().equals("discovery")){

            } else if (alert.getType().equals("autoregistration")){

            }

        } catch (JsonProcessingException e) {
            log.error("Unable to convert Zabbix alert: " + alertFromZabbix + "; to Java object: " + e);
        }
    }


    public void sendProblemView(Alert alert) {
        for (SendMessage message : viewService.setProblemView(alert)) {
            producerService.produceAnswer(message);
        }
    }
}
