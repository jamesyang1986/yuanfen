package com.yf.yuanfen.dto;

public class SmsRequest {
    private String phone;

    public SmsRequest(String phone) { this.phone = phone; }
    public String getPhone() { return phone; }
}
