package com.quest_exfo.backend.repository;

import com.quest_exfo.backend.entity.KaKaoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KaKaoUserRepository extends JpaRepository<KaKaoUser, Long> {
    Optional<KaKaoUser> findByKakaoId(Long kakaoId);
}
