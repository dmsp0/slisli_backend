package com.quest_exfo.backend.entity;


import com.quest_exfo.backend.common.BoothCategory;
import com.quest_exfo.backend.common.BoothType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Booth {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long boothId;
  private Integer videoRoomId;
  private Long memberId;
  private String title;
  private String info;

  @Enumerated(EnumType.STRING)
  private BoothCategory category;

  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;
  private String imgPath;
  private Integer maxPeople;
  private String openerName;

  @Enumerated(EnumType.STRING)
  private BoothType type;  // 기업 / 개인을 나타내는 필드 추가
}