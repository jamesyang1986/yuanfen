package com.yf.yuanfen.auth.controller;

import com.yf.yuanfen.auth.dto.SmsRequest;
import com.yf.yuanfen.auth.sms.SmsService;
import com.yf.yuanfen.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/sms")
public class SmsController {

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public ApiResponse<Void> sendCode(@Valid @RequestBody SmsRequest req) {
        smsService.sendCode(req.getPhone());
        return ApiResponse.success(null);
    }
}
