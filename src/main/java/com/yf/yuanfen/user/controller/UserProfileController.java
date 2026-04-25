package com.yf.yuanfen.user.controller;

import com.yf.yuanfen.common.ApiResponse;
import com.yf.yuanfen.user.dto.UserProfileDTO;
import com.yf.yuanfen.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/users")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileDTO> getProfile(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(userService.getUserProfile(userId));
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileDTO> updateProfile(@AuthenticationPrincipal Long userId,
                                                     @Valid @RequestBody UserProfileDTO dto) {
        return ApiResponse.success(userService.updateUserProfile(userId, dto));
    }

    @PostMapping("/avatar")
    public ApiResponse<Map<String, String>> uploadAvatar(@AuthenticationPrincipal Long userId,
                                                         @RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadAvatar(userId, file);
        Map<String, String> result = new ConcurrentHashMap();
        result.put("avatarUrl", avatarUrl);
        return ApiResponse.success(result);
    }
}
