package com.quest_exfo.backend.service.like;

import com.quest_exfo.backend.dto.request.LikeRequestDTO;
import com.quest_exfo.backend.dto.response.LikeResponseDTO;

import java.util.List;

public interface LikeService {

    void likeBooth(LikeRequestDTO likeRequestDTO);
    void unlikeBooth(LikeRequestDTO likeRequestDTO);
    List<LikeResponseDTO> getLikedBoothByMember(Long member_id);

    boolean checkIfLiked(LikeRequestDTO likeRequestDTO);
}