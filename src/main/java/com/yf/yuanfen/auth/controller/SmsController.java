package com.yf.yuanfen.auth.controller;

import com.yf.yuanfen.auth.dto.SmsRequest;
import com.yf.yuanfen.auth.sms.SmsService;
import com.yf.yuanfen.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "短信", description = "发送短信验证码")
@RestController
@RequestMapping("/api/v1/sms")
public class SmsController {

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @Operation(summary = "发送短信验证码", description = "同一手机号 60 秒内只能发送一次")
    @PostMapping("/send")
    public ApiResponse<Void> sendCode(@Valid @RequestBody SmsRequest req) {
        smsService.sendCode(req.getPhone());
        return ApiResponse.success(null);
    }
}
