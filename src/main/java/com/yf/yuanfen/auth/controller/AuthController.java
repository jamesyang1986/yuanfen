package com.yf.yuanfen.auth.controller;

import com.yf.yuanfen.auth.dto.*;
import com.yf.yuanfen.auth.login.LoginService;
import com.yf.yuanfen.auth.service.TokenService;
import com.yf.yuanfen.common.ApiResponse;
import com.yf.yuanfen.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "认证", description = "注册、登录、Token 刷新与登出")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final LoginService loginService;
    private final TokenService tokenService;

    public AuthController(UserService userService, LoginService loginService, TokenService tokenService) {
        this.userService = userService;
        this.loginService = loginService;
        this.tokenService = tokenService;
    }

    @Operation(summary = "手机号注册", description = "需先调用 /api/v1/sms/send 获取验证码")
    @PostMapping("/register/phone")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TokenResponse> registerByPhone(@Valid @RequestBody PhoneRegisterRequest req) {
        return ApiResponse.success(201, userService.registerByPhone(req));
    }

    @Operation(summary = "邮箱注册")
    @PostMapping("/register/email")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TokenResponse> registerByEmail(@Valid @RequestBody EmailRegisterRequest req) {
        return ApiResponse.success(201, userService.registerByEmail(req));
    }

    @Operation(summary = "用户名注册")
    @PostMapping("/register/username")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TokenResponse> registerByUsername(@Valid @RequestBody UsernameRegisterRequest req) {
        return ApiResponse.success(201, userService.registerByUsername(req));
    }

    @Operation(summary = "登录", description = "loginType 可选值：`USERNAME_PASSWORD` / `EMAIL_PASSWORD` / `PHONE_PASSWORD` / `PHONE_SMS`")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ApiResponse.success(loginService.login(req));
    }

    @Operation(summary = "刷新 Access Token", description = "使用 refreshToken 换取新的 accessToken")
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        return ApiResponse.success(tokenService.refreshAccessToken(req.getRefreshToken()));
    }

    @Operation(summary = "登出", description = "撤销指定 refreshToken，需携带 Authorization Bearer Token",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal Long userId,
                                    @Valid @RequestBody RefreshRequest req) {
        tokenService.revokeRefreshToken(req.getRefreshToken());
        return ApiResponse.success(null);
    }
}
