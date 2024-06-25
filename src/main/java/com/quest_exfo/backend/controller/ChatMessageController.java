package com.quest_exfo.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.quest_exfo.backend.entity.ChatMessage;
import com.quest_exfo.backend.repository.ChatMessageRepository;

import jakarta.annotation.security.PermitAll;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://js3.jsflux.co.kr")
@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    private final ChatMessageRepository chatMessageRepository;

    
    public ChatMessageController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @PostMapping("/save")
    @PermitAll
    public ResponseEntity<String> saveMessage(@RequestBody ChatMessage message) {
        chatMessageRepository.save(message);
        return ResponseEntity.status(HttpStatus.CREATED).body("Message saved successfully");
    }

}
