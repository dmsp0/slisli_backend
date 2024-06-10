package com.quest_exfo.backend.controller;

import com.quest_exfo.backend.dto.request.MemberSignupDTO;
import com.quest_exfo.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/send-code")
    public String sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.sendVerificationCode(email);
        return "Verification code sent to " + email;
    }

    @PostMapping("/verify-code")
    public String verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        boolean isValid = authService.verifyCode(email, code);
        if (isValid) {
            return "Verification successful";
        } else {
            return "Verification failed";
        }
    }

    @PostMapping("/signup")
    public String signUp(@RequestBody MemberSignupDTO signupDTO) {
        boolean isSignedUp = authService.signUp(signupDTO);
        if (isSignedUp) {
            return "Signup successful";
        } else {
            return "Signup failed";
        }
    }
}
