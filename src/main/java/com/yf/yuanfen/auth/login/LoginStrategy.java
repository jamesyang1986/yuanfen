package com.yf.yuanfen.auth.login;

import com.yf.yuanfen.auth.dto.LoginRequest;
import com.yf.yuanfen.auth.dto.TokenResponse;

public interface LoginStrategy {

    LoginType supports();

    TokenResponse login(LoginRequest request);
}
