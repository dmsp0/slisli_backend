package com.quest_exfo.backend.controller;

import com.quest_exfo.backend.dto.request.MemberSignupDTO;
import com.quest_exfo.backend.service.MailService;
import com.quest_exfo.backend.service.MemberServiceItf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mail") // 변경된 경로
public class MailController {

    @Autowired
    private MailService mailService;

    @Autowired
    private MemberServiceItf memberServiceItf;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        HttpStatus emailCheckStatus = memberServiceItf.checkEmailDuplicate(email);
        if (emailCheckStatus == HttpStatus.CONFLICT) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 이메일입니다.");
        }

        mailService.sendVerificationCode(email);
        return ResponseEntity.ok("Verification code sent to " + email);
    }

    @PostMapping("/verify-code")
    public String verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        boolean isValid = mailService.verifyCode(email, code);
        if (isValid) {
            return "Verification successful";
        } else {
            return "Verification failed";
        }
    }

    @PostMapping("/signup")
    public String signUp(@RequestBody MemberSignupDTO signupDTO) {
        boolean isSignedUp = mailService.signUp(signupDTO);
        if (isSignedUp) {
            return "Signup successful";
        } else {
            return "Signup failed";
        }
    }

}
