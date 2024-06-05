package com.quest_exfo.backend.controller;

import com.quest_exfo.backend.service.ExhibitionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/exhibition")
public class ExhibitionController {
  private final ExhibitionService exhibitionService;

  public ExhibitionController(ExhibitionService exhibitionService) {
    this.exhibitionService = exhibitionService;
  }

  @GetMapping("/fetch-exhibitions")
  public String fetchExhibitions(@RequestParam(defaultValue = "23") int totalPages, @RequestParam(defaultValue = "100") int perPage) {
    exhibitionService.fetchAndSaveExhibitions(totalPages, perPage);
    return "성공데스웅";
  }
}
