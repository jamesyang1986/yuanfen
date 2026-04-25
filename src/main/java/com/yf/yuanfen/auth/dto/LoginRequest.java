package com.yf.yuanfen.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(description = "登录请求")
public class LoginRequest {

    @Schema(
        description = "登录方式",
        example = "USERNAME_PASSWORD",
        allowableValues = {"USERNAME_PASSWORD", "EMAIL_PASSWORD", "PHONE_PASSWORD", "PHONE_SMS"}
    )
    @NotBlank(message = "loginType不能为空")
    private String loginType;

    @Schema(description = "登录标识（用户名 / 邮箱 / 手机号）", example = "alice_01")
    @NotBlank(message = "登录标识不能为空")
    private String identifier;

    @Schema(description = "凭证（密码或短信验证码）", example = "Password123")
    @NotBlank(message = "凭证不能为空")
    private String credential;

    public String getLoginType() { return loginType; }
    public void setLoginType(String loginType) { this.loginType = loginType; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getCredential() { return credential; }
    public void setCredential(String credential) { this.credential = credential; }
}
