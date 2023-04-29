package ru.ptkom.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.ptkom.dto.ManagementMessage;
import ru.ptkom.service.NodeControlService;

@Service
public class NodeControlServiceImpl implements NodeControlService {

    @Qualifier("RestTemplateWithoutAuth")
    private final RestTemplate restTemplate;

    public NodeControlServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    private final static String UPDATE = "update";


    String url = "http://localhost:8081/control";


    @Override
    public ResponseEntity sendCommandToUpdateAllowedChatIdListToNode() {

        ManagementMessage message = new ManagementMessage(UPDATE);
        ResponseEntity response = restTemplate.postForEntity(url, message, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response;

        } else {
            return ResponseEntity.internalServerError().body("Unable to update allowed chat id list on node " + response.getStatusCode());
        }
    }
}
