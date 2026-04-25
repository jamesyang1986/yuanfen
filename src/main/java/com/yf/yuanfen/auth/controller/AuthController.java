package com.yf.yuanfen.auth.controller;

import com.yf.yuanfen.auth.dto.*;
import com.yf.yuanfen.auth.login.LoginService;
import com.yf.yuanfen.auth.service.TokenService;
import com.yf.yuanfen.common.ApiResponse;
import com.yf.yuanfen.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PostMapping("/register/phone")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TokenResponse> registerByPhone(@Valid @RequestBody PhoneRegisterRequest req) {
        return ApiResponse.success(201, userService.registerByPhone(req));
    }

    @PostMapping("/register/email")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TokenResponse> registerByEmail(@Valid @RequestBody EmailRegisterRequest req) {
        return ApiResponse.success(201, userService.registerByEmail(req));
    }

    @PostMapping("/register/username")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TokenResponse> registerByUsername(@Valid @RequestBody UsernameRegisterRequest req) {
        return ApiResponse.success(201, userService.registerByUsername(req));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ApiResponse.success(loginService.login(req));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        return ApiResponse.success(tokenService.refreshAccessToken(req.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal Long userId,
                                    @Valid @RequestBody RefreshRequest req) {
        tokenService.revokeRefreshToken(req.getRefreshToken());
        return ApiResponse.success(null);
    }
}
