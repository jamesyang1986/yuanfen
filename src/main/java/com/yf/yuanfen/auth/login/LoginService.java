package com.yf.yuanfen.auth.login;

import com.yf.yuanfen.auth.dto.LoginRequest;
import com.yf.yuanfen.auth.dto.TokenResponse;
import com.yf.yuanfen.common.exception.BizException;
import com.yf.yuanfen.common.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LoginService {

    private final Map<LoginType, LoginStrategy> strategyMap;

    public LoginService(List<LoginStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(LoginStrategy::supports, Function.identity()));
    }

    public TokenResponse login(LoginRequest request) {
        LoginType type;
        try {
            type = LoginType.valueOf(request.getLoginType());
        } catch (IllegalArgumentException e) {
            throw new BizException(ErrorCode.MISSING_REQUIRED_FIELD);
        }
        LoginStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new BizException(ErrorCode.MISSING_REQUIRED_FIELD);
        }
        return strategy.login(request);
    }
}
