package com.yf.yuanfen.dto;

public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String tokenType;
    private boolean newUser;

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public long getExpiresIn() { return expiresIn; }
    public String getTokenType() { return tokenType; }
    public boolean isNewUser() { return newUser; }
}
