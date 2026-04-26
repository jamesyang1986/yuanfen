package com.yf.yuanfen.auth.wechat;

public class WechatUserInfo {
    private final String openid;
    private final String nickname;
    private final String avatarUrl;

    public WechatUserInfo(String openid, String nickname, String avatarUrl) {
        this.openid = openid;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }

    public String getOpenid() { return openid; }
    public String getNickname() { return nickname; }
    public String getAvatarUrl() { return avatarUrl; }
}
