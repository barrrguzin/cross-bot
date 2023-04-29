package ru.ptkom.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.ptkom.dto.AllowedChatId;

import java.util.Optional;

@Repository
public interface AllowedChatIdRepository extends CrudRepository<AllowedChatId, Long> {

    Optional<AllowedChatId> findByChatId(Long chatId);

    Iterable<AllowedChatId> findAll();
}
