package com.quest_exfo.backend.controller;

import com.quest_exfo.backend.dto.request.LikeRequestDTO;
import com.quest_exfo.backend.dto.response.LikeResponseDTO;
import com.quest_exfo.backend.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booths")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/like")
    public ResponseEntity<Void> likeBooth(@RequestBody LikeRequestDTO requestDTO){
        likeService.likeBooth(requestDTO);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/unlike")
    public ResponseEntity<Void> unlikeBooth(@RequestBody LikeRequestDTO requestDTO){
        likeService.unlikeBooth(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/likedList/{member_id}")
    public ResponseEntity<Page<LikeResponseDTO>> getLikedBoothByMember(
        @PathVariable Long member_id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String category) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LikeResponseDTO> likedBooths = likeService.getLikedBoothByMember(member_id, category, pageable);
        return ResponseEntity.ok(likedBooths);
    }

    @PostMapping("/liked")
    public ResponseEntity<Map<String, Boolean>> checkIfLiked(@RequestBody LikeRequestDTO requestDTO){
        boolean isLiked = likeService.checkIfLiked(requestDTO);
        Map<String, Boolean> response = new HashMap<>();
        response.put("liked",isLiked);
        return ResponseEntity.ok(response);
    }

}