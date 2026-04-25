package com.yf.yuanfen.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(description = "用户名注册请求")
public class UsernameRegisterRequest {

    @Schema(description = "用户名（3-20位字母/数字/下划线）", example = "alice_01")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名须为3-20位字母/数字/下划线")
    private String username;

    @Schema(description = "密码（8-32位）", example = "Password123")
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度须在8-32位之间")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
