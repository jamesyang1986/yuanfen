package com.yf.yuanfen.auth.controller;

import com.yf.yuanfen.auth.dto.TokenResponse;
import com.yf.yuanfen.auth.service.TokenService;
import com.yf.yuanfen.auth.wechat.WechatService;
import com.yf.yuanfen.auth.wechat.WechatUserInfo;
import com.yf.yuanfen.user.entity.User;
import com.yf.yuanfen.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/api/v1/auth/wechat")
public class WechatCallbackController {

    private final WechatService wechatService;
    private final UserMapper userMapper;
    private final TokenService tokenService;

    @Value("${wechat.redirect-base-url}")
    private String redirectBaseUrl;

    public WechatCallbackController(WechatService wechatService, UserMapper userMapper,
                                    TokenService tokenService) {
        this.wechatService = wechatService;
        this.userMapper = userMapper;
        this.tokenService = tokenService;
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code,
                         @RequestParam(required = false) String state,
                         HttpServletResponse response) throws IOException {
        WechatUserInfo info = wechatService.exchangeCode(code);

        User user = userMapper.selectByWechatOpenid(info.getOpenid());
        boolean isNewUser = (user == null);
        if (isNewUser) {
            user = new User();
            user.setWechatOpenid(info.getOpenid());
            userMapper.insert(user);
        }

        userMapper.updateLastLoginAt(user.getId());
        TokenResponse token = tokenService.generateTokenPair(user.getId());

        String redirect = redirectBaseUrl + "/#/wechat-callback"
                + "?token=" + token.getAccessToken()
                + "&refresh=" + token.getRefreshToken()
                + "&newUser=" + isNewUser;
        response.sendRedirect(redirect);
    }
}
