package com.quest_exfo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quest_exfo.backend.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    


}
