package com.quest_exfo.backend.service.like;

import com.quest_exfo.backend.common.BoothCategory;
import com.quest_exfo.backend.dto.request.LikeRequestDTO;
import com.quest_exfo.backend.dto.response.LikeResponseDTO;
import com.quest_exfo.backend.entity.Booth;
import com.quest_exfo.backend.entity.Like;
import com.quest_exfo.backend.entity.Member;
import com.quest_exfo.backend.repository.BoothRepository;
import com.quest_exfo.backend.repository.LikeRepository;
import com.quest_exfo.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService{

    private final LikeRepository likeRepository;
    private final BoothRepository boothRepository;
    private final MemberRepository memberRepository;
    @Override
    public void likeBooth(LikeRequestDTO likeRequestDTO) {
        Booth booth = boothRepository.findByBoothId(likeRequestDTO.getBoothId())
                .orElseThrow(()-> new IllegalArgumentException("BoothId 오류"));
        Member member = memberRepository.findById(likeRequestDTO.getMember_id())
                .orElseThrow(()-> new IllegalArgumentException("MemberId 오류"));

        if(!likeRepository.findByBoothAndMember(booth, member).isPresent()){
            Like like = Like.builder()
                    .booth(booth)
                    .member(member)
                    .build();
            likeRepository.save(like);
        }
    }

    @Override
    public void unlikeBooth(LikeRequestDTO likeRequestDTO) {
        Booth booth = boothRepository.findByBoothId(likeRequestDTO.getBoothId())
                .orElseThrow(()-> new IllegalArgumentException("BoothId 오류"));
        Member member = memberRepository.findById(likeRequestDTO.getMember_id())
                .orElseThrow(()-> new IllegalArgumentException("MemberId 오류"));

        likeRepository.findByBoothAndMember(booth, member)
                .ifPresent(likeRepository::delete);
    }

    @Override
    public Page<LikeResponseDTO> getLikedBoothByMember(Long member_id, String category, Pageable pageable) {
        Member member = memberRepository.findById(member_id)
            .orElseThrow(() -> new IllegalArgumentException("MemberId 오류"));

        Page<Like> likes;
        if (category == null || category.isEmpty()) {
            likes = likeRepository.findByMember(member, pageable);
        } else {
            BoothCategory boothCategory = BoothCategory.valueOf(category);
            likes = likeRepository.findByMemberAndBooth_Category(member, boothCategory, pageable);
        }

        return likes.map(like -> {
            Booth booth = like.getBooth();
            return LikeResponseDTO.builder()
                .likeId(like.getLike_id())
                .boothId(booth.getBoothId())
                .member_id(like.getMember().getMember_id())
                .title(booth.getTitle())
                .startTime(booth.getStartTime())
                .endTime(booth.getEndTime())
                .category(booth.getCategory())
                .imgPath(booth.getImgPath())
                .build();
        });
    }

    @Override
    public boolean checkIfLiked(LikeRequestDTO likeRequestDTO) {
        Booth booth = boothRepository.findByBoothId(likeRequestDTO.getBoothId())
                .orElseThrow(()-> new IllegalArgumentException("BoothId 오류"));
        Member member = memberRepository.findById(likeRequestDTO.getMember_id())
                .orElseThrow(()-> new IllegalArgumentException("MemberId 오류"));

        return likeRepository.findByBoothAndMember(booth, member).isPresent();
    }

}