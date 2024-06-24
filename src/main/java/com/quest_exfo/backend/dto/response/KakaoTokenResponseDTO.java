package com.quest_exfo.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoTokenResponseDTO {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("refresh_token_expires_in")
    private Long refreshTokenExpiresIn;

    @JsonProperty("id_token")
    private String idToken;
}