package com.yf.yuanfen.auth.wechat;

public interface WechatService {

    /**
     * 构建微信 OAuth 授权 URL
     */
    String buildAuthUrl(String redirectUri, String state);

    /**
     * 用授权码换取用户信息（openid、昵称、头像）
     */
    WechatUserInfo exchangeCode(String code);
}
