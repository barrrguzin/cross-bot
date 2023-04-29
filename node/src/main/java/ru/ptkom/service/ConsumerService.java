package ru.ptkom.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ptkom.model.Alert;

public interface ConsumerService {
    void consumeTextMessageUpdates(Update update);
    void consumeDocumentMessageUpdates(Update update);
    void consumePictureMessageUpdates(Update update);
    void consumeCallbackUpdates(Update update);
    void consumeZabbixAlert(String string);
}
