package com.yf.yuanfen.auth.sms;

public interface SmsService {

    void sendCode(String phone);

    boolean verifyCode(String phone, String code);
}
