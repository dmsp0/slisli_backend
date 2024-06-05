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

    private String password; //현재 비밀번호
    private String newPassword; //변경할 비밀번호
    private String newPasswordCheck; //변경할 비밀번호 확인
    private String newName; //변경할 이름

    @Builder
    public MemberUpdateDTO(String password, String newPassword, String newPasswordCheck, String newName) {
        this.password = password;
        this.newPassword = newPassword;
        this.newPasswordCheck = newPasswordCheck;
        this.newName = newName;
    }
}
