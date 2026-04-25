package com.yf.yuanfen.dto;

public class UsernameRegisterRequest {
    private String username;
    private String password;

    public UsernameRegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
