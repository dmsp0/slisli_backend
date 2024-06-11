package com.quest_exfo.backend.controller;

import com.quest_exfo.backend.common.BoothCategory;
import com.quest_exfo.backend.common.BoothType;
import com.quest_exfo.backend.common.ResourceNotFoundException;
import com.quest_exfo.backend.dto.request.BoothDTO;
import com.quest_exfo.backend.entity.Booth;
import com.quest_exfo.backend.repository.BoothRepository;
import com.quest_exfo.backend.service.booth.BoothService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/booths")
public class BoothController {

  @Autowired
  private BoothService boothService;

  @Autowired
  private BoothRepository boothRepository;

  @PostMapping("/insert")
  public Booth createBooth(
      @RequestPart("booth") BoothDTO boothDTO,
      @RequestPart("file") MultipartFile file) throws IOException {
    return boothService.createBooth(boothDTO, file);
  }

  @GetMapping("/get")
  public Page<Booth> getBooths(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) BoothCategory category,
      @RequestParam(required = false) BoothType type) {
    Pageable pageable = PageRequest.of(page, size);
    if (category != null && type != null) {
      return boothRepository.findByCategoryAndTypeOrderByDateDesc(category, type, pageable);
    } else if (category != null) {
      return boothRepository.findByCategoryOrderByDateDesc(category, pageable);
    } else if (type != null) {
      return boothRepository.findByTypeOrderByDateDesc(type, pageable);
    } else {
      return boothRepository.findAll(pageable);
    }
  }




  @GetMapping("/get/{id}")
  public Booth getBoothById(@PathVariable Long id) {
    return boothRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booth not found"));
  }
}