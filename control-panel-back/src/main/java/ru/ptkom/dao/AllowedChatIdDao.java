package ru.ptkom.dao;


import org.springframework.stereotype.Component;
import ru.ptkom.dto.AllowedChatId;
import ru.ptkom.exception.AllowedChatIdNotFoundException;
import ru.ptkom.repository.AllowedChatIdRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Component
public class AllowedChatIdDao {


    private final AllowedChatIdRepository allowedChatIdRepository;

    public AllowedChatIdDao(AllowedChatIdRepository allowedChatIdRepository) {
        this.allowedChatIdRepository = allowedChatIdRepository;
    }


    public AllowedChatId getById(Long chatId) throws AllowedChatIdNotFoundException {
        Optional<AllowedChatId> gotAllowedChatId = allowedChatIdRepository.findByChatId(chatId);

        if (gotAllowedChatId.isPresent()) {
            return gotAllowedChatId.get();
        } else {
            throw new AllowedChatIdNotFoundException("There is no Chat Id: " + chatId + " in allowed list!");
        }
    }

    public Iterable<AllowedChatId> getAllAllowedChatId() throws AllowedChatIdNotFoundException {

        Iterable<AllowedChatId> allowedChatIds = allowedChatIdRepository.findAll();

        if (allowedChatIds.iterator() != null) {
            return allowedChatIds;
        } else {
            throw new AllowedChatIdNotFoundException("There is no Chat Id in allowed list!");
        }
    }


    public void addChatIdToAllowed(AllowedChatId allowedChatId) {
        if (allowedChatId.getChatId() != null) {
            allowedChatIdRepository.save(allowedChatId);
        } else {
            throw new NullPointerException("Can not add new Chat Id in allowed list, Chat Id is null!");
        }
    }


    public void updateChatIdInAllowed(AllowedChatId updateChatId) throws AllowedChatIdNotFoundException {

        Long chatId = updateChatId.getChatId();

        if (chatId != null) {
            Optional<AllowedChatId> existingAllowedChatId = allowedChatIdRepository.findByChatId(chatId);
            if (existingAllowedChatId.isPresent()) {
                AllowedChatId allowedChatId = existingAllowedChatId.get();
                allowedChatId.setChatId(chatId);
                allowedChatId.setDescription(updateChatId.getDescription());
                allowedChatIdRepository.save(allowedChatId);
            } else {
                throw new AllowedChatIdNotFoundException("Can not update Chat Id in allowed list. Chat Id: " + chatId + " is not exist before!");
            }
        } else {
            throw new NullPointerException("Can not update Chat Id in allowed list, Chat Id is null!");
        }
    }


    public void deleteChatIdFromAllowed(AllowedChatId toRemoveChatId) throws AllowedChatIdNotFoundException {

        Long chatId = toRemoveChatId.getChatId();

        if (chatId != null) {
            Optional<AllowedChatId> existingAllowedChatId = allowedChatIdRepository.findByChatId(chatId);
            if (existingAllowedChatId.isPresent()) {
                allowedChatIdRepository.delete(existingAllowedChatId.get());
            } else {
                throw new AllowedChatIdNotFoundException("Can not delete Chat Id from allowed list. Chat Id: " + chatId + " is not exist before!");
            }
        } else {
            throw new NullPointerException("Can not delete Chat Id from allowed list, Chat Id is null!");
        }
    }
}