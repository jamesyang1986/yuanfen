package com.yf.yuanfen.auth.dto;

import javax.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "loginType不能为空")
    private String loginType;

    @NotBlank(message = "登录标识不能为空")
    private String identifier;

    @NotBlank(message = "凭证不能为空")
    private String credential;

    public String getLoginType() { return loginType; }
    public void setLoginType(String loginType) { this.loginType = loginType; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getCredential() { return credential; }
    public void setCredential(String credential) { this.credential = credential; }
}
