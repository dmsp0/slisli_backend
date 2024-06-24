package com.quest_exfo.backend.controller;

import com.quest_exfo.backend.dto.request.MemberDeleteDTO;
import com.quest_exfo.backend.dto.request.MemberLoginDTO;
import com.quest_exfo.backend.dto.request.MemberSignupDTO;
import com.quest_exfo.backend.dto.request.MemberUpdateDTO;
import com.quest_exfo.backend.dto.response.MemberResponseDTO;
import com.quest_exfo.backend.dto.response.MemberTokenDTO;
import com.quest_exfo.backend.entity.Member;
import com.quest_exfo.backend.service.AuthService;
import com.quest_exfo.backend.service.member.MemberServiceImpl;
import com.quest_exfo.backend.service.member.MemberService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberServiceItf;
    private final MemberServiceImpl memberService;
    private final AuthService authService;

    @GetMapping("/test")
    public String testEndpoint() {
        return "Hello from /test endpoint!";
    }
    @GetMapping("/checkEmail")
    public ResponseEntity<?> checkEmailDuplicate(@RequestParam String email){
        memberServiceItf.checkEmailDuplicate(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDTO> signup(@RequestBody MemberSignupDTO memberSignupDTO){
        MemberResponseDTO successSignup = memberServiceItf.signup(memberSignupDTO);
        System.out.println("회원가입 성공");
        return ResponseEntity.status(HttpStatus.CREATED).body(successSignup);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @PostMapping("/login")
    public ResponseEntity<MemberTokenDTO> login(@RequestBody MemberLoginDTO memberLoginDTO){
        MemberTokenDTO loginToken = memberServiceItf.login(memberLoginDTO);
        System.out.println("로그인 성공");
        return ResponseEntity.status(HttpStatus.OK).header(loginToken.getToken()).body(loginToken);
    }

    @PostMapping("/checkPassword")
    public ResponseEntity<MemberResponseDTO> checkPwd(@AuthenticationPrincipal Member member, @RequestBody Map<String, String> request){
        String password = request.get("password");
        MemberResponseDTO memberCurrent = memberServiceItf.check(member, password);
        return ResponseEntity.status(HttpStatus.OK).body(memberCurrent);
    }

    @PostMapping("/update")
    public ResponseEntity<MemberResponseDTO> update(@AuthenticationPrincipal Member member,
                                                    @RequestPart("member_profile") MemberUpdateDTO memberUpdateDTO,
                                                    @RequestPart("file") MultipartFile file) throws IOException {
        MemberResponseDTO memberUpdate = memberServiceItf.update(member, memberUpdateDTO, file);
        return ResponseEntity.status(HttpStatus.OK).body(memberUpdate);
    }


    @PostMapping("/delete")
    public ResponseEntity<MemberResponseDTO> delete(@AuthenticationPrincipal Member member, @RequestBody MemberDeleteDTO memberDeleteDTO){
        MemberResponseDTO deletedMember = memberService.delete(member, memberDeleteDTO);
        return ResponseEntity.status(HttpStatus.OK).body(deletedMember);
    }
    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<MemberTokenDTO> kakaoCallback(@RequestParam("code") String code) {
        MemberTokenDTO tokenDTO = authService.handleKakaoCallback(code);
        return ResponseEntity.ok().body(tokenDTO);
    }
}