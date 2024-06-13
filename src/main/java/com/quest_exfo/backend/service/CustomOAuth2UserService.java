package com.quest_exfo.backend.service;

import com.quest_exfo.backend.entity.Member;
import com.quest_exfo.backend.repository.MemberRepository;
import com.quest_exfo.backend.security.oauth2.KakaoUserDetails;
import com.quest_exfo.backend.security.oauth2.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo;

        if ("google".equals(provider)) {
            // Google OAuth2 처리
            oAuth2UserInfo = null; // 실제 GoogleUserDetails 클래스를 정의하고 여기서 초기화하세요
        } else if ("kakao".equals(provider)) {
            log.info("카카오 로그인");
            oAuth2UserInfo = new KakaoUserDetails(oAuth2User.getAttributes());
        } else {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + provider);
        }

        Member user = processOAuth2User(oAuth2UserInfo);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "email"
        );
    }

    private Member processOAuth2User(OAuth2UserInfo oAuth2UserInfo) {
        Member user = memberRepository.findByEmail(oAuth2UserInfo.getEmail())
                .orElseGet(() -> {
                    log.info("No existing user found with email: {}, creating new user", oAuth2UserInfo.getEmail());
                    return new Member();
                });

        user.setEmail(oAuth2UserInfo.getEmail());
        user.setName(oAuth2UserInfo.getName());
        user.setProvider(oAuth2UserInfo.getProvider());
        user.setProviderId(oAuth2UserInfo.getProviderId());
        user.setProfileImage(oAuth2UserInfo.getName());

        Member savedUser = memberRepository.save(user);
        log.info("Saved Kakao user with email: {}", savedUser.getEmail());

        return savedUser;
    }
}
