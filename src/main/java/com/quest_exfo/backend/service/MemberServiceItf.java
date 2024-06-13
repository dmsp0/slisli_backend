package com.quest_exfo.backend.service;

import com.quest_exfo.backend.dto.request.MemberLoginDTO;
import com.quest_exfo.backend.dto.request.MemberSignupDTO;
import com.quest_exfo.backend.dto.request.MemberUpdateDTO;
import com.quest_exfo.backend.dto.response.MemberResponseDTO;
import com.quest_exfo.backend.dto.response.MemberTokenDTO;
import com.quest_exfo.backend.entity.Member;
import org.springframework.http.HttpStatus;

public interface MemberServiceItf {
    HttpStatus checkEmailDuplicate(String email);
    MemberResponseDTO signup(MemberSignupDTO memberSignupDTO);
    MemberTokenDTO login(MemberLoginDTO memberLoginDTO);
    MemberResponseDTO check(Member member, String password);
    MemberResponseDTO update(Member member, MemberUpdateDTO updateDto);
}