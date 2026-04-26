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
import com.yf.yuanfen.user.dto.UserPublicDTO;
import com.yf.yuanfen.user.dto.PageResult;
import com.yf.yuanfen.user.entity.User;
import com.yf.yuanfen.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final FileStorageService fileStorageService;

    public UserService(UserMapper userMapper, SmsService smsService,
                       PasswordEncoder passwordEncoder, TokenService tokenService,
                       FileStorageService fileStorageService) {
        this.userMapper = userMapper;
        this.smsService = smsService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.fileStorageService = fileStorageService;
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
        String birthDateStr = UserProfileDTO.fromBirthLocalDate(user.getBirthDate());
        dto.setBirthDate(birthDateStr);
        dto.setCity(user.getCity());
        dto.setAddress(user.getAddress());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setOccupation(user.getOccupation());
        dto.setBio(user.getBio());
        dto.setPartnerTags(splitTags(user.getPartnerTags()));
        dto.setAge(calcAge(user.getBirthDate()));
        dto.setWechatId(user.getWechatId());
        dto.setQqNumber(user.getQqNumber());
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
        user.setOccupation(dto.getOccupation());
        user.setBio(dto.getBio());
        user.setPartnerTags(joinTags(dto.getPartnerTags()));
        user.setWechatId(dto.getWechatId());
        user.setQqNumber(dto.getQqNumber());
        userMapper.updateProfile(user);
        return getUserProfile(userId);
    }

    public String uploadAvatar(Long userId, MultipartFile file) {
        String avatarUrl = fileStorageService.storeAvatar(userId, file);
        userMapper.updateAvatarUrl(userId, avatarUrl);
        return avatarUrl;
    }

    public PageResult<UserPublicDTO> listUsers(int page, int size) {
        int offset = page * size;
        List<User> users = userMapper.listUsers(offset, size);
        long total = userMapper.countUsers();
        List<UserPublicDTO> items = users.stream().map(this::toPublicDTO).collect(Collectors.toList());
        return new PageResult<>(total, page, size, items);
    }

    public UserPublicDTO getUserById(Long id) {
        User user = userMapper.selectProfileById(id);
        if (user == null) throw new BizException(ErrorCode.USER_NOT_FOUND);
        return toPublicDTO(user);
    }

    private UserPublicDTO toPublicDTO(User user) {
        UserPublicDTO dto = new UserPublicDTO();
        dto.setId(user.getId());
        dto.setNickname(user.getNickname());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setCity(user.getCity());
        dto.setBirthDate(user.getBirthDate() != null ? user.getBirthDate().toString().substring(0, 7) : null);
        dto.setAge(calcAge(user.getBirthDate()));
        dto.setOccupation(user.getOccupation());
        dto.setBio(user.getBio());
        dto.setPartnerTags(splitTags(user.getPartnerTags()));
        dto.setWechatId(user.getWechatId());
        dto.setQqNumber(user.getQqNumber());
        return dto;
    }

    private Integer calcAge(java.time.LocalDate birthDate) {
        if (birthDate == null) return null;
        return Year.now().getValue() - birthDate.getYear();
    }

    private List<String> splitTags(String raw) {
        if (raw == null || raw.isEmpty()) return Collections.emptyList();
        return Arrays.asList(raw.split(","));
    }

    private String joinTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) return null;
        return String.join(",", tags);
    }
}
