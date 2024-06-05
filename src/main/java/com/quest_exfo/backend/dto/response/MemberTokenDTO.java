package com.quest_exfo.backend.dto.response;

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

    // fromEntity 메서드를 @Builder 패턴을 사용하여 간결하게 변경
    public static MemberTokenDTO fromEntity(UserDetails userDetails, String token, String refreshToken, String name) {
        return MemberTokenDTO.builder()
                .email(userDetails.getUsername())
                .name(name)
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

}

