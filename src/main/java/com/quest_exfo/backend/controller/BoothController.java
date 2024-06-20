package com.quest_exfo.backend.controller;

import com.quest_exfo.backend.common.BoothCategory;
import com.quest_exfo.backend.common.BoothType;
import com.quest_exfo.backend.common.ResourceNotFoundException;
import com.quest_exfo.backend.dto.request.BoothDTO;
import com.quest_exfo.backend.entity.Booth;
import com.quest_exfo.backend.repository.BoothRepository;
import com.quest_exfo.backend.service.booth.BoothService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/booths")
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
      @RequestParam(required = false) BoothType type,
      @RequestParam(required = false) String search) {
    Pageable pageable = PageRequest.of(page, size);

    if (category != null && type != null) {
      if (search != null && !search.isEmpty()) {
        return boothRepository.findByCategoryAndTypeAndSearch(category, type, search, pageable);
      }
      return boothRepository.findByCategoryAndTypeOrderByDateDesc(category, type, pageable);
    } else if (category != null) {
      if (search != null && !search.isEmpty()) {
        return boothRepository.findByCategoryAndSearch(category, search, pageable);
      }
      return boothRepository.findByCategoryOrderByDateDesc(category, pageable);
    } else if (type != null) {
      if (search != null && !search.isEmpty()) {
        return boothRepository.findByTypeAndSearch(type, search, pageable);
      }
      return boothRepository.findByTypeOrderByDateDesc(type, pageable);
    } else {
      if (search != null && !search.isEmpty()) {
        return boothRepository.findBySearch(search, pageable);
      }
      return boothRepository.findAll(pageable);
    }
  }

  @GetMapping("/get/{id}")
  public BoothDTO getBoothById(@PathVariable Long id) {
    Booth booth = boothRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booth not found"));
    System.out.println("Fetched Booth: " + booth);
    return convertToDTO(booth);
  }

  @GetMapping("/getRoomId/{boothId}")
  public ResponseEntity<Integer> getRoomId(@PathVariable Long boothId) {
    Booth booth = boothService.findBoothById(boothId);
    if (booth != null && booth.getVideoRoomId() != null) {
      return ResponseEntity.ok(booth.getVideoRoomId());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  private BoothDTO convertToDTO(Booth booth) {
    BoothDTO boothDTO = new BoothDTO();
    boothDTO.setBoothId(booth.getBoothId());
    boothDTO.setTitle(booth.getTitle());
    boothDTO.setInfo(booth.getInfo());
    boothDTO.setCategory(booth.getCategory());
    boothDTO.setDate(booth.getDate());
    boothDTO.setStartTime(booth.getStartTime());
    boothDTO.setEndTime(booth.getEndTime());
    boothDTO.setImgPath(booth.getImgPath());
    boothDTO.setMaxPeople(booth.getMaxPeople());
    boothDTO.setOpenerName(booth.getOpenerName());
    boothDTO.setType(booth.getType());
    boothDTO.setVideoRoomId(booth.getVideoRoomId());
    boothDTO.setMemberId(booth.getMemberId());
    System.out.println("Converted BoothDTO: " + boothDTO);
    return boothDTO;
  }

  @DeleteMapping("delete/{boothId}")
  public void deleteBoothByBoothId(@PathVariable Long boothId) {
    boothService.deleteByBoothId(boothId);
  }

  @PostMapping("/update/{boothId}")
  public Booth updateBooth(
      @PathVariable Long boothId,
      @RequestPart("booth") BoothDTO boothDTO,
      @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
    return boothService.updateBooth(boothId, boothDTO, file);
  }

  @GetMapping("/get_my/{memberId}")
  public Page<Booth> getBoothsByMemberAndCategory(
      @PathVariable Long memberId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "") String category) {
    return boothService.getBoothsByMemberAndCategory(memberId, category, page, size);
  }

  @GetMapping("/top-liked-by-category")
  public Map<String, Booth> getTopLikedBoothsByCategory() {
    LocalDate currentDate = LocalDate.now();
    return boothService.getTopLikedBoothsByCategoryAndDate(currentDate);
  }

  @GetMapping("/latest-by-category")
  public Map<String, Booth> getLatestBoothsByCategory() {
    return boothService.getLatestBoothsByCategory();
  }

  }