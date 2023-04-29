package ru.ptkom.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AllowedChatId {

    @Id
    private Long chatId;
    private String description;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = Long.parseLong(chatId);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean equals(AllowedChatId allowedChatId) {

        if (allowedChatId == null || getClass() != allowedChatId.getClass()) {
            return false;
        }

        Long chatId = allowedChatId.getChatId();
        if (this.getChatId() == chatId) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public String toString() {
        return "AllowedChatId{" +
                "chatId=" + chatId +
                ", description='" + description + '\'' +
                '}';
    }
}