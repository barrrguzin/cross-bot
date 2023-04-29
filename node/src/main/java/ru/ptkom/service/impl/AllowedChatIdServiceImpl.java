package ru.ptkom.service.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ptkom.dao.AllowedChatIdDAO;
import ru.ptkom.model.AllowedChatId;
import ru.ptkom.service.AllowedChatIdService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class AllowedChatIdServiceImpl implements AllowedChatIdService {

    private final AllowedChatIdDAO allowedChatIdDAO;

    public AllowedChatIdServiceImpl(AllowedChatIdDAO allowedChatIdDAO) {
        this.allowedChatIdDAO = allowedChatIdDAO;
    }

    private static List<Long> ALLOWED_CHAT_ID_LIST;

    @Override
    public List<Long> getAllAllowedChatIds() {
        return ALLOWED_CHAT_ID_LIST;
    }

    @Override
    public boolean isAllowed(Long chatId) {
        return ALLOWED_CHAT_ID_LIST.contains(chatId);
    }

    @Override
    public boolean isAllowed(String chatIdString) {
        Long chatId = Long.parseLong(chatIdString);
        return ALLOWED_CHAT_ID_LIST.contains(chatId);
    }

    @Override
    @Scheduled(fixedRate=1800000)
    @PostConstruct
    public void updateAllowedChatIdList() {
        Iterable<AllowedChatId> allowedChatIds = allowedChatIdDAO.getAllAllowedChatId();
        ALLOWED_CHAT_ID_LIST = StreamSupport.stream(allowedChatIds.spliterator(), false)
                .map(AllowedChatId::getChatId)
                .collect(Collectors.toList());

    }
}
