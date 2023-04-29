package ru.ptkom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ptkom.model.ManagementMessage;
import ru.ptkom.service.AllowedChatIdService;

import javax.annotation.PostConstruct;

@RestController
public class ManagementController {


    private final AllowedChatIdService allowedChatIdService;

    public ManagementController(AllowedChatIdService allowedChatIdService) {
        this.allowedChatIdService = allowedChatIdService;
    }


    @PostMapping("/control")
    public ResponseEntity updateAllowedChatIdList(@RequestBody ManagementMessage message) {

        String action = message.getAction();

        if (action.equals("update")) {
            allowedChatIdService.updateAllowedChatIdList();
            return ResponseEntity.ok().build();

        } else if (action.equals("something")) {
            return ResponseEntity.badRequest().build();

        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
