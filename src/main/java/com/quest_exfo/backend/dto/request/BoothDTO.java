package com.quest_exfo.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quest_exfo.backend.common.BoothCategory;
import com.quest_exfo.backend.common.BoothType;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoothDTO {
  private String title;
  private String info;
  private BoothCategory category;
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;
  private String imgPath;
  private Integer maxPeople;
  private String openerName;
  private BoothType type;
  private Integer videoRoomId;

  @JsonProperty("member_id")
  private Long memberId;
}