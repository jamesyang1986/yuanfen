package com.yf.yuanfen.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(description = "手机号注册请求")
public class PhoneRegisterRequest {

    @Schema(description = "手机号", example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    @Schema(description = "短信验证码（6位数字）", example = "123456")
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码格式错误")
    private String smsCode;

    @Schema(description = "密码（8-32位）", example = "Password123")
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度须在8-32位之间")
    private String password;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSmsCode() { return smsCode; }
    public void setSmsCode(String smsCode) { this.smsCode = smsCode; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
