package ru.ptkom.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ptkom.dao.AllowedChatIdDao;
import ru.ptkom.dto.AllowedChatId;
import ru.ptkom.exception.AllowedChatIdNotFoundException;
import ru.ptkom.service.NodeControlService;


@RestController
public class ChatIdController {


    private final AllowedChatIdDao allowedChatIdDao;
    private final NodeControlService nodeControlService;

    public ChatIdController(AllowedChatIdDao allowedChatIdDao, NodeControlService nodeControlService) {
        this.allowedChatIdDao = allowedChatIdDao;
        this.nodeControlService = nodeControlService;
    }


    //@CrossOrigin(origins = "http://localhost:4200")
    @CrossOrigin
    @GetMapping("/")
    public ResponseEntity<Iterable<AllowedChatId>> getAllAllowedChatIds() {

        try {
            Iterable<AllowedChatId> result = allowedChatIdDao.getAllAllowedChatId();
            return ResponseEntity.ok(result);

        } catch (AllowedChatIdNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }


    @CrossOrigin
    @PostMapping("/add")
    public ResponseEntity addChatIdToAllowedList(@RequestBody AllowedChatId allowedChatId) {

        try {
            allowedChatIdDao.addChatIdToAllowed(allowedChatId);
            nodeControlService.sendCommandToUpdateAllowedChatIdListToNode();
            return ResponseEntity.ok().build();

        } catch (NullPointerException e) {
            return ResponseEntity.noContent().build();
        }
    }


    @CrossOrigin
    @PostMapping("/update")
    public ResponseEntity updateChatIdParametersInAllowedList(@RequestBody AllowedChatId allowedChatId) {

        try {
            allowedChatIdDao.updateChatIdInAllowed(allowedChatId);
            nodeControlService.sendCommandToUpdateAllowedChatIdListToNode();
            return ResponseEntity.ok().build();

        } catch (AllowedChatIdNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @CrossOrigin
    @PostMapping("/delete")
    public ResponseEntity deleteChatIdFromAllowedList(@RequestBody AllowedChatId allowedChatId) {

        try {
            allowedChatIdDao.deleteChatIdFromAllowed(allowedChatId);
            nodeControlService.sendCommandToUpdateAllowedChatIdListToNode();
            return ResponseEntity.ok().build();

        } catch (AllowedChatIdNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @CrossOrigin
    @PostMapping("/refresh")
    public ResponseEntity updateNodeAllowedChatIdList() {

        ResponseEntity response = nodeControlService.sendCommandToUpdateAllowedChatIdListToNode();
        return response;
    }


}