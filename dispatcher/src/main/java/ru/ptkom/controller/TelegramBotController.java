package ru.ptkom.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBotController extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(TelegramBotController.class);

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    private UpdateController updateController;

    public TelegramBotController(UpdateController updateController) {
        this.updateController = updateController;
    }

    @PostConstruct
    public void connectUpdateAndTelegramBotControllers() {
        updateController.registerBot(this);

        ArrayList<BotCommand> listOfCommands = new ArrayList<BotCommand>();
        //listOfCommands.add(new BotCommand("/start", "Начать работу"));
        listOfCommands.add(new BotCommand("unavailable_hosts_list", "Список неактивных устройств"));
        listOfCommands.add(new BotCommand("monitored_hosts_list", "Список наблюдаемых устройств"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), "en"));
        } catch (TelegramApiException e) {
            log.error("Menu output error: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }



    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
        log.info("Got new update: " + update.toString());
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Unable to send message: " + e + "; Message: " + message);
            }
        }
    }

}