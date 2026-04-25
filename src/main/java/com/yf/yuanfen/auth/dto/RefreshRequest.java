package com.yf.yuanfen.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(description = "Token 刷新 / 登出请求")
public class RefreshRequest {

    @Schema(description = "登录时返回的 refreshToken", example = "eyJhbGciOiJIUzI1NiJ9...")
    @NotBlank(message = "refreshToken不能为空")
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
