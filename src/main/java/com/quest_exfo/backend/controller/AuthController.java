package com.quest_exfo.backend.controller;

import com.quest_exfo.backend.dto.request.MemberSignupDTO;
import com.quest_exfo.backend.dto.response.MemberTokenDTO;
import com.quest_exfo.backend.security.jwt.JwtTokenProvider;
import com.quest_exfo.backend.service.AuthService;
import com.quest_exfo.backend.service.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/refresh")
    public ResponseEntity<MemberTokenDTO> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        System.out.println("Received refresh token: " + refreshToken);  // 로그 추가

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            System.out.println("Invalid refresh token");  // 로그 추가
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        System.out.println("Username from token: " + username);  // 로그 추가
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtTokenProvider.createToken(userDetails.getUsername(), jwtTokenProvider.getRole(refreshToken));
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails.getUsername());

        MemberTokenDTO response = MemberTokenDTO.fromEntity(userDetails, newAccessToken, newRefreshToken);
        System.out.println("New tokens generated");  // 로그 추가
        return ResponseEntity.ok(response);
    }

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
