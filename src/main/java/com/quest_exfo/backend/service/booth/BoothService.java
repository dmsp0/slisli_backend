package com.quest_exfo.backend.service.booth;

import com.quest_exfo.backend.dto.request.BoothDTO;
import com.quest_exfo.backend.entity.Booth;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface BoothService {
  Booth createBooth(BoothDTO boothDTO, MultipartFile file) throws IOException;
  Booth updateBooth(Long boothId, BoothDTO boothDTO, MultipartFile file) throws IOException;

  Booth findBoothById(Long boothId);

  void deleteByBoothId(Long boothId);
  Page<Booth> getBoothsByMemberAndCategory(Long memberId, String category, int page, int size);


  Map<String, Booth> getTopLikedBoothsByCategoryAndDate(LocalDate date);

  Map<String, Booth> getLatestBoothsByCategory();

  long getTotalBoothsCount();

  long getUniqueBoothMembersCount();
}