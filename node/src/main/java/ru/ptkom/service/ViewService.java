package ru.ptkom.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ptkom.model.Alert;

import java.util.List;

public interface ViewService {

    public List<SendMessage> setProblemView(Alert alert);

    public List<SendMessage> setProblemRecoveryView(Alert alert);

    public List<SendMessage> setProblemUpdateView(Alert alert);

    public List<SendMessage> setDiscoveryView(Alert alert);

    public List<SendMessage> setAutoregistrationView(Alert alert);

    public SendMessage setIllegalUserView(Update update);

    public SendMessage setUnavailableCommandView(Update update);

    public SendMessage setStartView(Update update);

    public SendMessage setOutagesStatisticView(String chatId, String hostId);

    public SendMessage setListOfGraphsView(String chatId, String hostId, String hostName);
    public SendMessage setContactView(String chatId, String integrationId);
    public SendMessage setFullAlertMessageView(String eventId);
    public SendPhoto setGraphPngView(String chatId, String graphId);
}
