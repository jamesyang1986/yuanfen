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
        String phone = request.getIdentifier();
        String code = request.getCredential();

        boolean valid = smsService.verifyCode(phone, code);
        if (!valid) {
            throw new BizException(ErrorCode.INVALID_SMS_CODE);
        }

        User user = userMapper.selectByPhone(phone);
        boolean isNewUser = (user == null);
        if (isNewUser) {
            user = new User();
            user.setPhone(phone);
            userMapper.insert(user);
        }

        userMapper.updateLastLoginAt(user.getId());

        TokenResponse token = tokenService.generateTokenPair(user.getId());
        token.setNewUser(isNewUser);
        return token;
    }
}
