package com.quest_exfo.backend.entity;

import javax.persistence.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booth_id")
    private String boothId;

    private String nickname;

    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 생성자
    public ChatMessage() {
        this.createdAt = LocalDateTime.now();
    }

    public ChatMessage(String boothId, String nickname, String message) {
        this.boothId = boothId;
        this.nickname = nickname;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}
