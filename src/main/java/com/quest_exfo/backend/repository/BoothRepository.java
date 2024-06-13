package com.quest_exfo.backend.repository;

import com.quest_exfo.backend.common.BoothCategory;
import com.quest_exfo.backend.common.BoothType;
import com.quest_exfo.backend.entity.Booth;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoothRepository extends JpaRepository<Booth, Long> {


  //  Booth 엔티티에서 date가 주어진 날짜와 일치하는 videoroomId를 선택
  //  날짜에 사용된 모든 비디오룸 ID를 리스트 형태로 반환
  @Query("SELECT b.videoRoomId FROM Booth b WHERE b.date = :date")
  List<Integer> findVideoRoomIdsByDate(LocalDate date);


  Page<Booth> findByCategoryOrderByDateDesc(BoothCategory category, Pageable pageable);
  Page<Booth> findByCategoryAndTypeOrderByDateDesc(BoothCategory category, BoothType type, Pageable pageable);
  Page<Booth> findByTypeOrderByDateDesc(BoothType type, Pageable pageable);

}