package com.quest_exfo.backend.dto.response;

import com.quest_exfo.backend.dto.request.ExhibitionRequestDTO;
import lombok.Data;
import java.util.List;

@Data
public class ExhibitionResponseDTO {
  private int currentCount;
  private List<ExhibitionRequestDTO> data;
  private int matchCount;
  private int page;
  private int perPage;
  private int totalCount;
}