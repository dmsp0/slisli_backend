package com.quest_exfo.backend.service;

import com.quest_exfo.backend.dto.response.KakaoTokenResponseDTO;
import com.quest_exfo.backend.dto.response.KakaoUserInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    @Value("${kakao.api_url}")
    private String kakaoApiUrl;

    @Value("${kakao.token_url}")
    private String kakaoTokenUrl;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    public KakaoTokenResponseDTO getAccessTokenFromKakao(String code) {
        RestTemplate restTemplate = new RestTemplate();

        String requestUrl = UriComponentsBuilder.fromHttpUrl(kakaoTokenUrl)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("code", code)
                .toUriString();

        log.info("Requesting access token from Kakao with URL: {}", requestUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<KakaoTokenResponseDTO> response = restTemplate.postForEntity(
                    requestUrl,
                    headers,
                    KakaoTokenResponseDTO.class
            );

            log.info("Received response from Kakao: {}", response.getBody());

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Failed to get access token from Kakao: {}", e.getMessage());
            throw new RuntimeException("Failed to get access token from Kakao", e);
        }
    }

    public KakaoUserInfoResponseDTO getUserInfo(String accessToken) {
        WebClient webClient = WebClient.create(kakaoApiUrl);

        KakaoUserInfoResponseDTO userInfo = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v2/user/me").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDTO.class)
                .block();

        log.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.getId());
        log.info("[ Kakao Service ] Nickname ---> {} ", userInfo.getKakaoAccount().getProfile().getNickname());
//        log.info("[ Kakao Service ] ProfileImageUrl ---> {} ", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

        return userInfo;
    }
}