package com.quest_exfo.backend.service.booth;

import com.quest_exfo.backend.dto.request.BoothDTO;
import com.quest_exfo.backend.entity.Booth;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface BoothService {
  Booth createBooth(BoothDTO boothDTO, MultipartFile file) throws IOException;
  Booth findBoothById(Long boothId);
}
