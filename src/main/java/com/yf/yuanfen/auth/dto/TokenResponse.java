package com.yf.yuanfen.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token 响应")
public class TokenResponse {

    @Schema(description = "访问令牌（有效期 15 分钟）", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "刷新令牌（有效期 7 天）", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    @Schema(description = "accessToken 有效期（毫秒）", example = "900000")
    private long expiresIn;

    @Schema(description = "Token 类型", example = "Bearer")
    private String tokenType = "Bearer";

    public TokenResponse(String accessToken, String refreshToken, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public long getExpiresIn() { return expiresIn; }
    public String getTokenType() { return tokenType; }
}
