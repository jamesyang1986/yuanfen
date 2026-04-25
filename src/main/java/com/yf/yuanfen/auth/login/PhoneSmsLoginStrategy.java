package com.yf.yuanfen.auth.login;

import com.yf.yuanfen.auth.dto.TokenResponse;
import com.yf.yuanfen.auth.service.TokenService;
import com.yf.yuanfen.auth.sms.SmsService;
import com.yf.yuanfen.auth.dto.LoginRequest;
import com.yf.yuanfen.common.exception.BizException;
import com.yf.yuanfen.common.exception.ErrorCode;
import com.yf.yuanfen.user.entity.User;
import com.yf.yuanfen.user.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class PhoneSmsLoginStrategy implements LoginStrategy {

    private final UserMapper userMapper;
    private final SmsService smsService;
    private final TokenService tokenService;

    public PhoneSmsLoginStrategy(UserMapper userMapper, SmsService smsService, TokenService tokenService) {
        this.userMapper = userMapper;
        this.smsService = smsService;
        this.tokenService = tokenService;
    }

    @Override
    public LoginType supports() {
        return LoginType.PHONE_SMS;
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        User user = userMapper.selectByPhone(request.getIdentifier());
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        boolean valid = smsService.verifyCode(request.getIdentifier(), request.getCredential());
        if (!valid) {
            throw new BizException(ErrorCode.INVALID_SMS_CODE);
        }
        return tokenService.generateTokenPair(user.getId());
    }
}
