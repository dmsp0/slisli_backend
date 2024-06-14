package com.quest_exfo.backend.controller;

import com.quest_exfo.backend.dto.request.MemberSignupDTO;
import com.quest_exfo.backend.dto.response.KakaoTokenResponseDTO;
import com.quest_exfo.backend.dto.response.KakaoUserInfoResponseDTO;
import com.quest_exfo.backend.dto.response.MemberTokenDTO;
import com.quest_exfo.backend.entity.Member;
import com.quest_exfo.backend.security.jwt.JwtTokenProvider;
import com.quest_exfo.backend.service.AuthService;
import com.quest_exfo.backend.service.CustomUserDetails;
import com.quest_exfo.backend.service.CustomUserDetailsService;
import com.quest_exfo.backend.service.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    private final KakaoService kakaoService;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService; // CustomUserDetailsService 주입

    @PostMapping("/refresh")
    public MemberTokenDTO refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        System.out.println("Received refresh token: " + refreshToken);
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("리프레시토큰 만료 또는 무효");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        System.out.println("Extracted username: " + username);
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtTokenProvider.createToken(userDetails.getUsername(), jwtTokenProvider.getRole(refreshToken));
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails.getUsername());
        System.out.println("Generated new access token: " + newAccessToken);
        System.out.println("Generated new refresh token: " + newRefreshToken);

        return MemberTokenDTO.fromEntity(userDetails, newAccessToken, newRefreshToken);
    }

    @GetMapping("/kakao")
    public ResponseEntity<Map<String, String>> loginPage() {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;
        log.info("Redirecting to: {}", location);

        Map<String, String> response = new HashMap<>();
        response.put("url", location);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/callback")
    public RedirectView callback(@RequestParam("code") String code) {
        log.info("Callback with code: {}", code);

        try {
            KakaoTokenResponseDTO tokenResponse = kakaoService.getAccessTokenFromKakao(code);

            if (tokenResponse == null) {
                return new RedirectView("/error/401");
            }

            String accessToken = tokenResponse.getAccessToken();
            KakaoUserInfoResponseDTO userProfile = kakaoService.getUserInfo(accessToken);
            log.info("User profile: {}", userProfile);

            Member user = authService.registerOrLoginUser(userProfile);
            log.info("Processed user: {}", user);

            String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

            String redirectUrl = String.format("http://localhost:3000?token=%s&refreshToken=%s&email=%s&name=%s",
                    URLEncoder.encode(jwtToken, StandardCharsets.UTF_8.toString()),
                    URLEncoder.encode(refreshToken, StandardCharsets.UTF_8.toString()),
                    URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8.toString()),
                    URLEncoder.encode(user.getName(), StandardCharsets.UTF_8.toString()));

            return new RedirectView(redirectUrl);
        } catch (Exception e) {
            log.error("Error during Kakao authentication: {}", e.getMessage(), e);
            return new RedirectView("/error/500");
        }
    }
}