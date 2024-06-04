package com.quest_exfo.backend.service;

import com.quest_exfo.backend.dto.request.ExhibitionRequestDTO;
import com.quest_exfo.backend.dto.response.ExhibitionResponseDTO;
import com.quest_exfo.backend.entity.Exhibition;
import com.quest_exfo.backend.repository.ExhibitionRepository;
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

  public ExhibitionService(ExhibitionRepository exhibitionRepository, RestTemplate restTemplate) {
    this.exhibitionRepository = exhibitionRepository;
    this.restTemplate = restTemplate;
  }

  public void fetchAndSaveExhibitions(int totalPages, int perPage) {
    String serviceKey = "f/yYy8svm3t0PGLHgpkhrFhC6SYzwr4ZDpzGe9/6CYxCWCmGcRCh0iAeIArWF6hZHJ8CS/7NMAhWRB/V6Yjl3A==";

    for (int page = 1; page <= totalPages; page++) {
      String url = String.format("https://api.odcloud.kr/api/15042123/v1/uddi:4f4295f6-40e7-48fd-9a25-cb827b97dff6?page=%d&perPage=%d&serviceKey=%s", page, perPage, serviceKey);
      try {
        ResponseEntity<ExhibitionResponseDTO> responseEntity = restTemplate.getForEntity(url, ExhibitionResponseDTO.class);
        ExhibitionResponseDTO response = responseEntity.getBody();
        if (response != null) {
          List<Exhibition> exhibitions = response.getData().stream().map(this::convertToEntity).collect(Collectors.toList());
          exhibitionRepository.saveAll(exhibitions);
        }
      } catch (HttpClientErrorException e) {
        System.err.println("API 호출 오류: " + e.getStatusCode());
        System.err.println("오류 메시지: " + e.getResponseBodyAsString());
      }
    }
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
}