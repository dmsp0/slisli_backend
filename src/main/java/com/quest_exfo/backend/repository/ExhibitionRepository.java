package com.quest_exfo.backend.repository;

import com.quest_exfo.backend.entity.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
}