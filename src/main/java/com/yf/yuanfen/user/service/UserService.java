package com.yf.yuanfen.user.service;

import com.yf.yuanfen.auth.dto.EmailRegisterRequest;
import com.yf.yuanfen.auth.dto.PhoneRegisterRequest;
import com.yf.yuanfen.auth.dto.TokenResponse;
import com.yf.yuanfen.auth.dto.UsernameRegisterRequest;
import com.yf.yuanfen.auth.service.TokenService;
import com.yf.yuanfen.auth.sms.SmsService;
import com.yf.yuanfen.common.exception.BizException;
import com.yf.yuanfen.common.exception.ErrorCode;
import com.yf.yuanfen.user.dto.UserProfileDTO;
import com.yf.yuanfen.user.entity.User;
import com.yf.yuanfen.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserService(UserMapper userMapper, SmsService smsService,
                       PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userMapper = userMapper;
        this.smsService = smsService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public TokenResponse registerByPhone(PhoneRegisterRequest req) {
        boolean valid = smsService.verifyCode(req.getPhone(), req.getSmsCode());
        if (!valid) {
            throw new BizException(ErrorCode.INVALID_SMS_CODE);
        }
        if (userMapper.existsByPhone(req.getPhone())) {
            throw new BizException(ErrorCode.PHONE_ALREADY_EXISTS);
        }
        User user = new User();
        user.setPhone(req.getPhone());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        userMapper.insert(user);
        return tokenService.generateTokenPair(user.getId());
    }

    public TokenResponse registerByEmail(EmailRegisterRequest req) {
        if (userMapper.existsByEmail(req.getEmail())) {
            throw new BizException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        userMapper.insert(user);
        return tokenService.generateTokenPair(user.getId());
    }

    public TokenResponse registerByUsername(UsernameRegisterRequest req) {
        if (userMapper.existsByUsername(req.getUsername())) {
            throw new BizException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        userMapper.insert(user);
        return tokenService.generateTokenPair(user.getId());
    }

    public UserProfileDTO getUserProfile(Long userId) {
        User user = userMapper.selectProfileById(userId);
        UserProfileDTO dto = new UserProfileDTO();
        dto.setNickname(user.getNickname());
        dto.setGender(user.getGender());
        dto.setBirthDate(UserProfileDTO.fromBirthLocalDate(user.getBirthDate()));
        dto.setCity(user.getCity());
        dto.setAddress(user.getAddress());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }

    public UserProfileDTO updateUserProfile(Long userId, UserProfileDTO dto) {
        User user = new User();
        user.setId(userId);
        user.setNickname(dto.getNickname());
        user.setGender(dto.getGender());
        user.setBirthDate(dto.toBirthLocalDate());
        user.setCity(dto.getCity());
        user.setAddress(dto.getAddress());
        user.setAvatarUrl(dto.getAvatarUrl());
        userMapper.updateProfile(user);
        return getUserProfile(userId);
    }
}
