package com.quest_exfo.backend.dto.response;

import com.quest_exfo.backend.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class MemberResponseDTO {
    private String email;
    private String name;
    private Long member_id;
    private String profileImgPath; // 추가된 필드

    @Builder
    public MemberResponseDTO(String email, String name, Long member_id, String profileImgPath) {
        this.email = email;
        this.name = name;
        this.member_id = member_id;
        this.profileImgPath = profileImgPath;
    }

    public static MemberResponseDTO fromEntity(Member member) {
        return MemberResponseDTO.builder()
                .email(member.getEmail())
                .name(member.getName())
                .member_id(member.getMember_id())
                .profileImgPath(member.getProfileImgPath()) // 추가된 필드 매핑
                .build();
    }
}
