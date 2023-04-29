package ru.ptkom.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ptkom.dao.UserDataDAO;
import ru.ptkom.entity.UserData;
import ru.ptkom.service.AllowedChatIdService;
import ru.ptkom.service.MainService;
import ru.ptkom.service.ProducerService;
import ru.ptkom.service.ViewService;

import java.sql.Timestamp;

@Service
public class MainServiceImpl implements MainService {
    private static final Logger log = Logger.getLogger(MainServiceImpl.class);

    private final AllowedChatIdService allowedChatIdService;

    private final UserDataDAO userDataDAO;
    private final ProducerService producerService;
    private final ViewService viewService;

    private final ObjectMapper mapper = new ObjectMapper();

    public MainServiceImpl(AllowedChatIdService allowedChatIdService, UserDataDAO userDataDAO, ProducerService producerService, ViewService viewService) {
        this.allowedChatIdService = allowedChatIdService;
        this.userDataDAO = userDataDAO;
        this.producerService = producerService;
        this.viewService = viewService;
    }

    @Override
    public void processTextMessage(Update update) {

        String chatId = update.getMessage().getChatId().toString();

        if (!allowedChatIdService.isAllowed(chatId)){
            saveUniqueUserData(update);
            SendMessage sendMessage = viewService.setIllegalUserView(update);
            producerService.produceAnswer(sendMessage);
        }
        else if (update.getMessage().getText().equals("/start")){
            saveUniqueUserData(update);
            SendMessage sendMessage = viewService.setStartView(update);
            producerService.produceAnswer(sendMessage);
        }
        else {
            SendMessage sendMessage = viewService.setUnavailableCommandView(update);
            producerService.produceAnswer(sendMessage);
        }
    }

    @Override
    public void processCallbackMessage(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();


        try {
            JsonNode jsonNode = mapper.readTree(callbackData);
            String type = jsonNode.get("type").asText();
            if (type.equals("statistic")) {
                String hostId = jsonNode.get("id").asText();
                SendMessage answer = viewService.setOutagesStatisticView(chatId, hostId);
                producerService.produceAnswer(answer);
            } else if (type.equals("graphs")) {
                String hostId = jsonNode.get("id").asText();
                String hostName = jsonNode.get("name").asText();
                SendMessage answer = viewService.setListOfGraphsView(chatId, hostId, hostName);
                producerService.produceAnswer(answer);
            } else if (type.equals("graph")) {
                String graphId = jsonNode.get("id").asText();
                SendPhoto answer = viewService.setGraphPngView(chatId, graphId);
                producerService.produceAnswer(answer);
            } else if (type.equals("contact")) {
                String integrationId = jsonNode.get("integrationId").asText();
                SendMessage answer = viewService.setContactView(chatId, integrationId);
                producerService.produceAnswer(answer);
            } else if (type.equals("full")) {

            }
        } catch (Exception e) {
            log.error("Can't parse JSON from callback message:" + update + "; Error: " + e.getMessage());
        }


    }

    private void saveUniqueUserData(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();

        if (userDataDAO.findById(message.getChatId()).isEmpty()){
            User from = message.getFrom();
            UserData user = new UserData();
            user.setChatId(chatId);
            user.setUserStatus(0);
            user.setUserName(from.getUserName());
            user.setRegisterTime(String.valueOf(new Timestamp(System.currentTimeMillis())));
            user.setFirstName(from.getFirstName());
            user.setLastName(from.getLastName());
            user.setLanguageCode(from.getLanguageCode());
            user.setLocation(String.valueOf(message.getLocation()));
            userDataDAO.save(user);
            log.info("Data about user: " + message.getChatId() + ", saved to data base.");
        }
    }
}
