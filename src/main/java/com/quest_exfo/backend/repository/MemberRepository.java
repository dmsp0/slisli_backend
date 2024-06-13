package com.quest_exfo.backend.repository;

import com.quest_exfo.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByName(String name);
    Optional<Member> findById(Long member_id);
    boolean existsByEmail(String email);

    @Query("SELECT m.name FROM Member m WHERE m.email = :email")
    String findNameByEmail(@Param("email")String email);

}