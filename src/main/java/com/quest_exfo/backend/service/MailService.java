package com.quest_exfo.backend.service;


import com.quest_exfo.backend.dto.request.MemberSignupDTO;
import com.quest_exfo.backend.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    @Autowired
    private EmailService emailService;

    private Map<String, String> verificationCodes = new HashMap<>();

    public void sendVerificationCode(String email) {
        String code = emailService.generateRandomCode();
        emailService.sendEmail(email, "Your Verification Code", "Your code is: " + code);
        verificationCodes.put(email, code);
    }

    public boolean verifyCode(String email, String code) {
        String correctCode = verificationCodes.get(email);
        if (correctCode != null && correctCode.equals(code)) {
            verificationCodes.remove(email); // 인증 성공 후 코드 제거
            return true;
        }
        return false;
    }

    public boolean signUp(MemberSignupDTO signupDTO) {
        if (!signupDTO.getPassword().equals(signupDTO.getPasswordCheck())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (!verifyCode(signupDTO.getEmail(), signupDTO.getVerificationCode())) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        // 비밀번호 해싱
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(signupDTO.getPassword());
        signupDTO.setPassword(hashedPassword);

        // Member 엔티티로 변환 후 저장 (Repository를 통해 저장)
        Member member = MemberSignupDTO.ofEntity(signupDTO);
        // memberRepository.save(member); // 실제 저장 로직 추가 필요

        return true;
    }
}
