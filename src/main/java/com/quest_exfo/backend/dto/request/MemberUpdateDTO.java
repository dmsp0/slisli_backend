package com.quest_exfo.backend.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MemberUpdateDTO {
    //    정보 수정 요청
    private String email;
    private String password; //현재 비밀번호
    private String passwordCheck; //변경할 비밀번호 확인
    private String name; //변경할 이름
    private String profileImgPath;

    @Builder
    public MemberUpdateDTO(String email, String password, String passwordCheck, String name, String profileImgPath) {
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.name = name;
        this.profileImgPath = profileImgPath;
    }
}