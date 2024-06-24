package com.quest_exfo.backend.service.like;

import com.quest_exfo.backend.dto.request.LikeRequestDTO;
import com.quest_exfo.backend.dto.response.LikeResponseDTO;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikeService {

    void likeBooth(LikeRequestDTO likeRequestDTO);
    void unlikeBooth(LikeRequestDTO likeRequestDTO);
    Page<LikeResponseDTO> getLikedBoothByMember(Long member_id, String category, Pageable pageable);

    boolean checkIfLiked(LikeRequestDTO likeRequestDTO);
}