package ru.ptkom.dao;

import org.springframework.stereotype.Component;
import ru.ptkom.model.AllowedChatId;
import ru.ptkom.repository.AllowedChatIdRepository;

@Component
public class AllowedChatIdDAO {

    private final AllowedChatIdRepository allowedChatIdRepository;

    public AllowedChatIdDAO(AllowedChatIdRepository allowedChatIdRepository) {
        this.allowedChatIdRepository = allowedChatIdRepository;
    }


    public Iterable<AllowedChatId> getAllAllowedChatId() {
        Iterable<AllowedChatId> allowedChatIds = allowedChatIdRepository.findAll();
        return allowedChatIds;
    }
}
