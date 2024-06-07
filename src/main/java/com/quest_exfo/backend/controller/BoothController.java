package com.quest_exfo.backend.controller;

import com.quest_exfo.backend.dto.request.BoothDTO;
import com.quest_exfo.backend.entity.Booth;
import com.quest_exfo.backend.service.booth.BoothService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/booths")
public class BoothController {

  @Autowired
  private BoothService boothService;

  @PostMapping("/insert")
  public Booth createBooth(
      @RequestPart("booth") BoothDTO boothDTO,
      @RequestPart("file") MultipartFile file) throws IOException {
    return boothService.createBooth(boothDTO, file);
  }
}