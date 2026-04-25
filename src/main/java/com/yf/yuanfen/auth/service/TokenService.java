package com.yf.yuanfen.auth.service;

import com.yf.yuanfen.auth.dto.TokenResponse;
import com.yf.yuanfen.auth.util.JwtUtil;
import com.yf.yuanfen.common.exception.BizException;
import com.yf.yuanfen.common.exception.ErrorCode;
import com.yf.yuanfen.user.entity.RefreshToken;
import com.yf.yuanfen.user.mapper.RefreshTokenMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenMapper refreshTokenMapper;

    @Value("${jwt.access-token-expiry-ms}")
    private long accessTokenExpiryMs;

    @Value("${jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    public TokenService(JwtUtil jwtUtil, RefreshTokenMapper refreshTokenMapper) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenMapper = refreshTokenMapper;
    }

    public TokenResponse generateTokenPair(Long userId) {
        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshTokenValue = UUID.randomUUID().toString().replace("-", "");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiryMs / 1000));
        refreshToken.setRevoked(false);
        refreshTokenMapper.insert(refreshToken);

        return new TokenResponse(accessToken, refreshTokenValue, accessTokenExpiryMs / 1000);
    }

    public TokenResponse refreshAccessToken(String rawRefreshToken) {
        RefreshToken rt = refreshTokenMapper.selectByToken(rawRefreshToken);
        if (rt == null) {
            throw new BizException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        if (rt.isRevoked()) {
            throw new BizException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }
        if (LocalDateTime.now().isAfter(rt.getExpiresAt())) {
            throw new BizException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        String newAccessToken = jwtUtil.generateAccessToken(rt.getUserId());
        return new TokenResponse(newAccessToken, rawRefreshToken, accessTokenExpiryMs / 1000);
    }

    public void revokeRefreshToken(String rawRefreshToken) {
        refreshTokenMapper.revokeByToken(rawRefreshToken);
    }
}
