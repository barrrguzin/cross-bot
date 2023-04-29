package ru.ptkom.service;

import org.springframework.http.ResponseEntity;

public interface NodeControlService {

    ResponseEntity sendCommandToUpdateAllowedChatIdListToNode();
}
