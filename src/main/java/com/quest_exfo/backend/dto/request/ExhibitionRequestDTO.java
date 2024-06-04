package com.quest_exfo.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExhibitionRequestDTO {
  @JsonProperty("전시 분야")
  private String exhibitionField;

  @JsonProperty("전시 품목")
  private String exhibitionItem;

  @JsonProperty("전시회 개최건물")
  private String exhibitionBuilding;

  @JsonProperty("전시회 개최국가")
  private String exhibitionCountry;

  @JsonProperty("전시회 개최년도")
  private int exhibitionYear;

  @JsonProperty("전시회 개최도시")
  private String exhibitionCity;

  @JsonProperty("전시회 개최주기")
  private String exhibitionFrequency;

  @JsonProperty("전시회 모집기간 시작일")
  private String recruitmentStartDate;

  @JsonProperty("전시회 모집기간 종료 일")
  private String recruitmentEndDate;

  @JsonProperty("전시회 시작일")
  private String startDate;

  @JsonProperty("전시회 영문 약칭명")
  private String shortEnglishName;

  @JsonProperty("전시회 영문명 전체명")
  private String fullEnglishName;

  @JsonProperty("전시회 종료일")
  private String endDate;

  @JsonProperty("전시회 한글명")
  private String koreanName;

  @JsonProperty("한국참가 주관단체 주소")
  private String organizerAddress;

  @JsonProperty("한국참가 주관단체명")
  private String organizerName;
}
