package com.quest_exfo.backend.notused;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class Exhibition {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 500)
  private String exhibitionField;

  @Column(length = 1000)
  private String exhibitionItem;

  @Column(length = 500)
  private String exhibitionBuilding;

  @Column(length = 100)
  private String exhibitionCountry;

  private int exhibitionYear;

  @Column(length = 100)
  private String exhibitionCity;

  @Column(length = 100)
  private String exhibitionFrequency;

  @Column(length = 100)
  private String recruitmentStartDate;

  @Column(length = 100)
  private String recruitmentEndDate;

  @Column(length = 100)
  private String startDate;

  @Column(length = 200)
  private String shortEnglishName;

  @Column(length = 500)
  private String fullEnglishName;

  @Column(length = 100)
  private String endDate;

  @Column(length = 500)
  private String koreanName;

  @Column(length = 500)
  private String organizerAddress;

  @Column(length = 200)
  private String organizerName;
}