package ru.ptkom.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.ptkom.model.Alert;
import ru.ptkom.service.AllowedChatIdService;
import ru.ptkom.service.ViewService;
import ru.ptkom.service.ZabbixService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ViewServiceImpl implements ViewService {
    private static final Logger log = Logger.getLogger(ViewServiceImpl.class);

    private final AllowedChatIdService allowedChatIdService;

    private final ZabbixService zabbixService;

    private final ObjectMapper objectMapper;


    public ViewServiceImpl(AllowedChatIdService allowedChatIdService, ZabbixService zabbixService, ObjectMapper objectMapper) {
        this.allowedChatIdService = allowedChatIdService;
        this.zabbixService = zabbixService;
        this.objectMapper = objectMapper;
        List<BotCommand> listOfCommands = new ArrayList();
        listOfCommands.add(new BotCommand("/start", "Начать работу!"));
    }


    private static final String COUNT_OF_HOST_OUTAGES_TEXT = "Количество предупреждениц от устройства за месяц: %s";

    @Override
    public List<SendMessage> setProblemView(Alert alert) {

        ArrayList<Long> allowedChatIds = (ArrayList<Long>) allowedChatIdService.getAllAllowedChatIds();

        ArrayList<SendMessage> messagesList = new ArrayList<SendMessage>(allowedChatIds.size());

        String alertText = "Событие: "+alert.getName()+"\n" +
                "Проишествие обнаружено в "+alert.getTime()+", "+alert.getDate()+"\n" +
                "Устройство: "+alert.getHost()+"\n\n" +
                "Важность: "+alert.getSeverity()+"\n" +
                "Дополнительная информация: "+alert.getData()+"\n" +
                "Идентификатор в Zabbix: "+alert.getId()+"\n" +
                alert.getUrl();

        InlineKeyboardMarkup alertMessageButtons = setAlertButtons(alert);

        for(Long chatId : allowedChatIds) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(alertText);
            sendMessage.setReplyMarkup(alertMessageButtons);
            sendMessage.setChatId(chatId.toString());
            messagesList.add(sendMessage);
        }


        return messagesList;
    }

    @Override
    public List<SendMessage> setProblemRecoveryView(Alert alert) {

        ArrayList<Long> allowedChatIds = (ArrayList<Long>) allowedChatIdService.getAllAllowedChatIds();

        ArrayList<SendMessage> messagesList = new ArrayList<SendMessage>(allowedChatIds.size());

        String recoveryText = "Проишествие устранено за "+alert.getDuration()+" в "+alert.getRecoveryTime()+", "+alert.getDate()+"\n" +
                "Событие: "+alert.getName()+"\n" +
                "Устройство: "+alert.getHost()+"\n" +
                "Важность: "+alert.getSeverity()+"\n" +
                "Идентификатор в Zabbix: "+alert.getId()+"\n" +
                alert.getUrl();
        InlineKeyboardMarkup alertMessageButtons = setAlertButtons(alert);

        for(Long chatId : allowedChatIds) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(recoveryText);
            sendMessage.setReplyMarkup(alertMessageButtons);
            sendMessage.setChatId(chatId);
            messagesList.add(sendMessage);
        }


        return messagesList;
    }

    @Override
    public List<SendMessage> setProblemUpdateView(Alert alert) {
        return null;
    }

    @Override
    public List<SendMessage> setDiscoveryView(Alert alert) {
        return null;
    }

    @Override
    public List<SendMessage> setAutoregistrationView(Alert alert) {
        return null;
    }

    @Override
    public SendMessage setIllegalUserView(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Вам не разрешено пользоваться данным ботом. Вас нет в списке. Ваш ChatId: " + chatId);
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    @Override
    public SendMessage setUnavailableCommandView(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Команда " + update.getMessage().getText() + " не распознанна");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    @Override
    public SendMessage setStartView(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Ваш ChatID (" + chatId + ") найден в списке разрешенных пользователей.");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    @Override
    public SendMessage setOutagesStatisticView(String chatId, String hostId) {
        SendMessage sendMessage = new SendMessage();
        String outagesStatistic = zabbixService.getHostOutagesStatisticById(hostId);

        try {
            JsonNode jsonNode = objectMapper.readTree(outagesStatistic);
            String countOfHostOutages = jsonNode.get("result").asText();
            sendMessage.setText(String.format(COUNT_OF_HOST_OUTAGES_TEXT, countOfHostOutages));
        } catch (JsonProcessingException e) {
            sendMessage.setText(String.format(COUNT_OF_HOST_OUTAGES_TEXT, e.getMessage()));
        }

        sendMessage.setChatId(chatId);

        return sendMessage;
    }

    @Override
    public SendMessage setListOfGraphsView(String chatId, String hostId, String hostName) {
        SendMessage sendMessage = new SendMessage();
        String listOfGraphs = zabbixService.getListOfHostGraphsById(hostId);
        String listOfGraphsView = "Список доступных графиков для устройства "+ hostName +":";

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();

        try {
            JsonNode resultJsonNode = objectMapper.readTree(listOfGraphs).get("result");

            if (resultJsonNode.isArray()) {
                for (JsonNode graphNode : resultJsonNode) {

                    InlineKeyboardButton graphButton = new InlineKeyboardButton();
                    graphButton.setText(graphNode.get("name").asText());
                    graphButton.setCallbackData("{\"id\": \""+graphNode.get("graphid").asText()+"\", \"type\": \"graph\"}");
                    List<InlineKeyboardButton> buttonRow = new ArrayList<>(1);
                    buttonRow.add(graphButton);
                    rowList.add(buttonRow);

                }
                inlineKeyboardMarkup.setKeyboard(rowList);

            } else {
                sendMessage.setText("Для данного устройства графиков не обнаружено.");
            }

        } catch (JsonProcessingException e) {
            sendMessage.setText("Не удалось отобразить список графиков:" + e.getMessage());
        }

        sendMessage.setText(listOfGraphsView);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(chatId);

        return sendMessage;
    }

    @Override
    public SendMessage setContactView(String chatId, String integrationId) {
        SendMessage sendMessage = new SendMessage();
        //Здесь будет обращение к API какого-нибудь сервиса учета оборудования, в который и будут заносится телефон и контакты
        if (integrationId.equals("Not yet")) {
            sendMessage.setText("Пока не реализованно");
            sendMessage.setChatId(chatId);
        } else {
            sendMessage.setText("Не заспознанно");
            sendMessage.setChatId(chatId);
        }

        return sendMessage;
    }

    @Override
    public SendMessage setFullAlertMessageView(String eventId) {

//        String eventFullString = zabbixService.getFullAlertInfo(eventId);
//        JsonNode eventResult = objectMapper.readTree(eventFullString).get("result");
//        String object = eventResult.get("object").asText();
//        if (object.equals("0")) {
//
//        }

        return null;
    }

    @Override
    public SendPhoto setGraphPngView(String chatId, String graphId) {

        byte[] imageBytes = zabbixService.getGraphById(graphId);
        InputStream imageStream = new ByteArrayInputStream(imageBytes);
        InputFile imageFile = new InputFile(imageStream, graphId);

        SendPhoto message = new SendPhoto();
        message.setPhoto(imageFile);
        message.setChatId(chatId);


        return message;
    }

    private String getChatId(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        return chatId;
    }

    private InlineKeyboardMarkup setAlertButtons(Alert alert) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> getOutageStatisticRow = makeOneRowInlineKeyboardButton("Колличество отказов узла за месяц", "{\"id\": \""+alert.getHostId()+"\", \"type\": \"statistic\"}");
        List<InlineKeyboardButton> getGraphsListRow = makeOneRowInlineKeyboardButton("Список графиков узла", "{\"id\": \""+alert.getHostId()+"\", \"type\": \"graphs\", \"name\": \""+alert.getHost()+"\"}");
        List<InlineKeyboardButton> getHostLocationContactData = makeOneRowInlineKeyboardButton("Контактные данные по узлу связи", "{\"id\": \""+alert.getHostId()+"\", \"type\": \"contact\", \"integrationId: \"Not yet\"}");
        //List<InlineKeyboardButton> getFullMessage = makeOneRowInlineKeyboardButton("Расширенная информация", "{\"id\": \""+alert.getHostId()+"\", \"type\": \"full\"}");

        List<List<InlineKeyboardButton>> rowList= new ArrayList<List<InlineKeyboardButton>>(3);
        rowList.add(getHostLocationContactData);
        rowList.add(getOutageStatisticRow);
        rowList.add(getGraphsListRow);
        //rowList.add(getFullMessage);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    private List<InlineKeyboardButton> makeOneRowInlineKeyboardButton(String text, String value) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(value);
        List<InlineKeyboardButton> buttonRow = new ArrayList<>(1);
        buttonRow.add(button);
        return buttonRow;
    }
}
