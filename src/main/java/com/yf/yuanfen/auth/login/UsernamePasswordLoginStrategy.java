package com.yf.yuanfen.auth.login;

import com.yf.yuanfen.auth.dto.LoginRequest;
import com.yf.yuanfen.auth.dto.TokenResponse;
import com.yf.yuanfen.auth.service.TokenService;
import com.yf.yuanfen.common.exception.BizException;
import com.yf.yuanfen.common.exception.ErrorCode;
import com.yf.yuanfen.user.entity.User;
import com.yf.yuanfen.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsernamePasswordLoginStrategy implements LoginStrategy {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UsernamePasswordLoginStrategy(UserMapper userMapper, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    public LoginType supports() {
        return LoginType.USERNAME_PASSWORD;
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        User user = userMapper.selectByUsername(request.getIdentifier());
        if (user == null || !passwordEncoder.matches(request.getCredential(), user.getPasswordHash())) {
            throw new BizException(ErrorCode.INVALID_CREDENTIALS);
        }
        return tokenService.generateTokenPair(user.getId());
    }
}
