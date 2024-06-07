package com.quest_exfo.backend.notused;

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