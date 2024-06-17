package com.quest_exfo.backend.repository;

import com.quest_exfo.backend.common.BoothCategory;
import com.quest_exfo.backend.entity.Booth;
import com.quest_exfo.backend.entity.Like;
import com.quest_exfo.backend.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByBoothAndMember(Booth booth, Member member);
    List<Like> findByMember(Member member);
    Page<Like> findByMember(Member member, Pageable pageable);
    Page<Like> findByMemberAndBooth_Category(Member member, BoothCategory category, Pageable pageable);
}