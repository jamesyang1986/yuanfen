package com.yf.yuanfen.auth.controller;

import com.yf.yuanfen.auth.wechat.WechatService;
import com.yf.yuanfen.common.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/wechat")
public class WechatAuthUrlController {

    private final WechatService wechatService;

    @Value("${wechat.redirect-base-url}")
    private String redirectBaseUrl;

    public WechatAuthUrlController(WechatService wechatService) {
        this.wechatService = wechatService;
    }

    @GetMapping("/auth-url")
    public ApiResponse<Map<String, String>> getAuthUrl(
            @RequestParam(required = false, defaultValue = "") String state) {
        String callbackUri = redirectBaseUrl + "/api/v1/auth/wechat/callback";
        String url = wechatService.buildAuthUrl(callbackUri, state);
        return ApiResponse.success(Map.of("url", url));
    }
}
