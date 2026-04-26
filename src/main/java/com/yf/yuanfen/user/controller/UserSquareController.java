package com.yf.yuanfen.user.controller;

import com.yf.yuanfen.common.ApiResponse;
import com.yf.yuanfen.user.dto.PageResult;
import com.yf.yuanfen.user.dto.UserPublicDTO;
import com.yf.yuanfen.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserSquareController {

    private final UserService userService;

    public UserSquareController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<PageResult<UserPublicDTO>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(userService.listUsers(page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserPublicDTO> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }
}
