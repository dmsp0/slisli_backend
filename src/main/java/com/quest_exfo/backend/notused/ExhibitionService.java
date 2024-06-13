package com.quest_exfo.backend.notused;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExhibitionService {
  private final ExhibitionRepository exhibitionRepository;
  private final RestTemplate restTemplate;

  @Value("${exhibition.apiKey}")
  private String apiKey;

  public ExhibitionService(ExhibitionRepository exhibitionRepository, RestTemplate restTemplate) {
    this.exhibitionRepository = exhibitionRepository;
    this.restTemplate = restTemplate;
  }

  public void fetchAndSaveExhibitions(int totalPages, int perPage) {

    for (int page = 1; page <= totalPages; page++) {
      String url = String.format("https://api.odcloud.kr/api/15042123/v1/uddi:4f4295f6-40e7-48fd-9a25-cb827b97dff6?page=%d&perPage=%d&serviceKey=%s", page, perPage, apiKey);
      try {
        ResponseEntity<ExhibitionResponseDTO> responseEntity = restTemplate.getForEntity(url, ExhibitionResponseDTO.class);
        ExhibitionResponseDTO response = responseEntity.getBody();
        if (response != null) {
          List<Exhibition> exhibitions = response.getData().stream()
              .map(this::saveOrUpdate)
              .collect(Collectors.toList());
          exhibitionRepository.saveAll(exhibitions);
        }
      } catch (HttpClientErrorException e) {
        System.err.println("API 호출 오류: " + e.getStatusCode());
        System.err.println("오류 메시지: " + e.getResponseBodyAsString());
      }
    }
  }

  private Exhibition saveOrUpdate(ExhibitionRequestDTO dto) {
    List<Exhibition> existingExhibitions = exhibitionRepository.findByExhibitionYearAndOrganizerNameAndFullEnglishNameAndStartDate(
        dto.getExhibitionYear(), dto.getOrganizerName(), dto.getFullEnglishName(), dto.getStartDate());

    Exhibition exhibition;
    if (!existingExhibitions.isEmpty()) {
      exhibition = existingExhibitions.get(0);
      updateExhibition(exhibition, dto);
    } else {
      exhibition = convertToEntity(dto);
    }

    return exhibition;
  }

  private Exhibition convertToEntity(ExhibitionRequestDTO dto) {
    Exhibition exhibition = new Exhibition();
    exhibition.setExhibitionField(dto.getExhibitionField());
    exhibition.setExhibitionItem(dto.getExhibitionItem());
    exhibition.setExhibitionBuilding(dto.getExhibitionBuilding());
    exhibition.setExhibitionCountry(dto.getExhibitionCountry());
    exhibition.setExhibitionYear(dto.getExhibitionYear());
    exhibition.setExhibitionCity(dto.getExhibitionCity());
    exhibition.setExhibitionFrequency(dto.getExhibitionFrequency());
    exhibition.setRecruitmentStartDate(dto.getRecruitmentStartDate());
    exhibition.setRecruitmentEndDate(dto.getRecruitmentEndDate());
    exhibition.setStartDate(dto.getStartDate());
    exhibition.setShortEnglishName(dto.getShortEnglishName());
    exhibition.setFullEnglishName(dto.getFullEnglishName());
    exhibition.setEndDate(dto.getEndDate());
    exhibition.setKoreanName(dto.getKoreanName());
    exhibition.setOrganizerAddress(dto.getOrganizerAddress());
    exhibition.setOrganizerName(dto.getOrganizerName());
    return exhibition;
  }

  private void updateExhibition(Exhibition exhibition, ExhibitionRequestDTO dto) {
    exhibition.setExhibitionField(dto.getExhibitionField());
    exhibition.setExhibitionItem(dto.getExhibitionItem());
    exhibition.setExhibitionBuilding(dto.getExhibitionBuilding());
    exhibition.setExhibitionCountry(dto.getExhibitionCountry());
    exhibition.setExhibitionYear(dto.getExhibitionYear());
    exhibition.setExhibitionCity(dto.getExhibitionCity());
    exhibition.setExhibitionFrequency(dto.getExhibitionFrequency());
    exhibition.setRecruitmentStartDate(dto.getRecruitmentStartDate());
    exhibition.setRecruitmentEndDate(dto.getRecruitmentEndDate());
    exhibition.setStartDate(dto.getStartDate());
    exhibition.setShortEnglishName(dto.getShortEnglishName());
    exhibition.setFullEnglishName(dto.getFullEnglishName());
    exhibition.setEndDate(dto.getEndDate());
    exhibition.setKoreanName(dto.getKoreanName());
    exhibition.setOrganizerAddress(dto.getOrganizerAddress());
    exhibition.setOrganizerName(dto.getOrganizerName());
  }
}
