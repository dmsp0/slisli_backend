package com.quest_exfo.backend.service;

import com.quest_exfo.backend.common.Role;
import com.quest_exfo.backend.dto.request.MemberSignupDTO;
import com.quest_exfo.backend.dto.response.KakaoTokenResponseDTO;
import com.quest_exfo.backend.dto.response.KakaoUserInfoResponseDTO;
import com.quest_exfo.backend.dto.response.MemberTokenDTO;
import com.quest_exfo.backend.entity.Member;
import com.quest_exfo.backend.repository.MemberRepository;
import com.quest_exfo.backend.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailService emailService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoService kakaoService;
    private final JwtTokenProvider jwtTokenProvider;

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
        String hashedPassword = passwordEncoder.encode(signupDTO.getPassword());
        signupDTO.setPassword(hashedPassword);

        // Member 엔티티로 변환 후 저장
        Member member = MemberSignupDTO.ofEntity(signupDTO);
        memberRepository.save(member);

        return true;
    }

    public Member registerOrLoginUser(KakaoUserInfoResponseDTO userProfile) {
        String email = userProfile.getKakaoAccount().getEmail();
        String provider = "kakao";
        String providerId = String.valueOf(userProfile.getId());

        // 이미 존재하는 사용자가 있으면 로그인 처리
        Optional<Member> existingUser = memberRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            Member user = existingUser.get();
            user.setProvider(provider);
            user.setProviderId(providerId);
            return memberRepository.save(user);
        }

        // 새로운 사용자 등록
        Member newUser = Member.builder()
                .email(email)
                .password(passwordEncoder.encode("default_password")) // 임의의 비밀번호 설정
                .name(userProfile.getKakaoAccount().getProfile().getNickname())
                .role(Role.USER) // 기본 역할 설정
                .provider(provider)
                .providerId(providerId)
                .build();
        return memberRepository.save(newUser);
    }

    public MemberTokenDTO handleKakaoCallback(String code) {
        KakaoTokenResponseDTO tokenResponse = kakaoService.getAccessTokenFromKakao(code);

        if (tokenResponse == null) {
            throw new RuntimeException("Failed to get access token from Kakao");
        }

        String accessToken = tokenResponse.getAccessToken();
        KakaoUserInfoResponseDTO userProfile = kakaoService.getUserInfo(accessToken);

        Member user = registerOrLoginUser(userProfile);

        String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return MemberTokenDTO.builder()
                .email(user.getEmail())
                .name(user.getName())
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}