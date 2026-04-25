package com.yf.yuanfen.user.controller;

import com.yf.yuanfen.common.ApiResponse;
import com.yf.yuanfen.user.dto.UserProfileDTO;
import com.yf.yuanfen.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Tag(name = "用户资料", description = "个人资料查看、更新及头像上传")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/users")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "获取当前用户个人资料")
    @GetMapping("/profile")
    public ApiResponse<UserProfileDTO> getProfile(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(userService.getUserProfile(userId));
    }

    @Operation(summary = "更新个人资料", description = "所有字段均为可选，仅传入需要修改的字段")
    @PutMapping("/profile")
    public ApiResponse<UserProfileDTO> updateProfile(@AuthenticationPrincipal Long userId,
                                                     @Valid @RequestBody UserProfileDTO dto) {
        return ApiResponse.success(userService.updateUserProfile(userId, dto));
    }

    @Operation(
        summary = "上传头像",
        description = "接受 JPEG / PNG / GIF / WebP 格式，文件大小不超过 5 MB。" +
                      "上传成功后自动更新用户的 avatarUrl 字段并返回可访问的图片 URL。"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "上传成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
            description = "文件类型不支持或超过 5 MB 大小限制",
            content = @Content(schema = @Schema(example = "{\"code\":400,\"message\":\"不支持的文件类型\"}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    })
    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public ApiResponse<Map<String, String>> uploadAvatar(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "头像图片文件（JPEG/PNG/GIF/WebP，≤ 5MB）", required = true)
            @RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadAvatar(userId, file);
        Map<String, String> result = new ConcurrentHashMap<>();
        result.put("avatarUrl", avatarUrl);
        return ApiResponse.success(result);
    }
}
