package com.quest_exfo.backend.dto.response;

import com.quest_exfo.backend.service.CustomUserDetails;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

@Setter
@Getter
@Builder
public class MemberTokenDTO {
    //로그인 요청 -> 응답용DTO
    //토큰+리프레쉬토큰 건내주기

    private String email;
    private String name;
    private String token;
    private String refreshToken;
    private Long member_id;
    private String profileImgPath; // 프로필 이미지 경로 추가

    // fromEntity 메서드를 @Builder 패턴을 사용하여 간결하게 변경
    public static MemberTokenDTO fromEntity(CustomUserDetails customUserDetails, String token, String refreshToken) {
        return MemberTokenDTO.builder()
            .email(customUserDetails.getUsername())
            .name(customUserDetails.getName())
            .token(token)
            .member_id(customUserDetails.getMember_id())
            .refreshToken(refreshToken)
            .profileImgPath(customUserDetails.getProfileImgPath()) // 프로필 이미지 경로 추가
            .build();
    }
}
