package com.yf.yuanfen.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Schema(description = "邮箱注册请求")
public class EmailRegisterRequest {

    @Schema(description = "邮箱地址", example = "user@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    private String email;

    @Schema(description = "密码（8-32位）", example = "Password123")
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度须在8-32位之间")
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
