package com.yf.yuanfen.auth.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class SmsRequest {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
