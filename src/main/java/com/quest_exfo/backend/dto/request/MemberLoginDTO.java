package com.quest_exfo.backend.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberLoginDTO {
    //    로그인 요청
//    이메일+비밀번호로 로그인
    private String email;
    private String password;

    @Builder
    public MemberLoginDTO(String email, String password){
        this.email=email;
        this.password=password;
    }
}
