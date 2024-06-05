package com.quest_exfo.backend.dto.request;

import com.quest_exfo.backend.common.Role;
import com.quest_exfo.backend.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberSignupDTO {
    //    회원가입 요청

    private String email;
    private String password;
    private String passwordCheck;
    private String name;

    @Builder
    public MemberSignupDTO(String email, String password, String passwordCheck, String name){
        this.email=email;
        this.password=password;
        this.passwordCheck=passwordCheck;
        this.name=name;
    }

    public static Member ofEntity(MemberSignupDTO dto){
        return Member.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .name(dto.getName())
                .role(Role.USER)
                .build();
    }
}
