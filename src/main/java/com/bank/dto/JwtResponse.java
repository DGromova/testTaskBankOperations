package com.bank.dto;

import lombok.Getter;
import lombok.Setter;

public class JwtResponse {
    private String token;
    @Setter
    @Getter
    private String username;

    public JwtResponse(String accessToken, String username) {
        this.token = accessToken;
        this.username = username;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

}
