package com.yf.yuanfen.dto;

public class LoginRequest {
    private String loginType;
    private String identifier;
    private String credential;

    public LoginRequest(String loginType, String identifier, String credential) {
        this.loginType = loginType;
        this.identifier = identifier;
        this.credential = credential;
    }
}
