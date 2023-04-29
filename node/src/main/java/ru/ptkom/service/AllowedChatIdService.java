package ru.ptkom.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AllowedChatIdService {

    List<Long> getAllAllowedChatIds();

    boolean isAllowed(Long chatId);

    boolean isAllowed(String chatId);

    void updateAllowedChatIdList();
}
