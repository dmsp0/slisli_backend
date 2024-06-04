package com.quest_exfo.backend.repository;

import com.quest_exfo.backend.controller.ExhibitionController;
import com.quest_exfo.backend.entity.Exhibition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
  List<Exhibition> findByExhibitionYearAndOrganizerNameAndFullEnglishNameAndStartDate(int exhibitionYear, String organizerName, String fullEnglishName, String startDate);
}