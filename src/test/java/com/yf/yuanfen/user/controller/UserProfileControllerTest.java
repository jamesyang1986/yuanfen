package com.yf.yuanfen.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yf.yuanfen.auth.util.JwtUtil;
import com.yf.yuanfen.config.SecurityConfig;
import com.yf.yuanfen.user.dto.UserProfileDTO;
import com.yf.yuanfen.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
@Import(SecurityConfig.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    private static final Long USER_ID = 42L;

    private UsernamePasswordAuthenticationToken userAuth() {
        return new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());
    }

    // 8.1: 未登录访问 GET 返回 401
    @Test
    void getProfile_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isUnauthorized());
    }

    // 8.1: 已登录 GET 返回 200，空字段为 null
    @Test
    void getProfile_authenticated_returns200WithNullFields() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        when(userService.getUserProfile(USER_ID)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/users/profile")
                        .with(authentication(userAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").isEmpty())
                .andExpect(jsonPath("$.data.gender").isEmpty())
                .andExpect(jsonPath("$.data.birthDate").isEmpty());
    }

    // 8.1: 未登录访问 PUT 返回 401
    @Test
    void updateProfile_unauthenticated_returns401() throws Exception {
        mockMvc.perform(put("/api/v1/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    // 8.1: 已登录 PUT 更新成功返回 200
    @Test
    void updateProfile_authenticated_returns200() throws Exception {
        UserProfileDTO request = new UserProfileDTO();
        request.setNickname("小明");
        request.setAvatarUrl("https://example.com/avatar.jpg");

        UserProfileDTO response = new UserProfileDTO();
        response.setNickname("小明");
        response.setAvatarUrl("https://example.com/avatar.jpg");

        when(userService.updateUserProfile(eq(USER_ID), any(UserProfileDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/profile")
                        .with(authentication(userAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("小明"))
                .andExpect(jsonPath("$.data.avatarUrl").value("https://example.com/avatar.jpg"));
    }

    // 8.2: 非法 birthDate 格式返回 400
    @Test
    void updateProfile_invalidBirthDate_returns400() throws Exception {
        String body = "{\"birthDate\": \"not-a-date\"}";

        mockMvc.perform(put("/api/v1/users/profile")
                        .with(authentication(userAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // 8.3: nickname 超过 32 字符返回 400
    @Test
    void updateProfile_nicknameTooLong_returns400() throws Exception {
        String longNickname = "a".repeat(33);
        String body = "{\"nickname\": \"" + longNickname + "\"}";

        mockMvc.perform(put("/api/v1/users/profile")
                        .with(authentication(userAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // avatar upload: 上传成功返回 200 及 avatarUrl
    @Test
    void uploadAvatar_authenticated_returns200WithUrl() throws Exception {
        String expectedUrl = "http://localhost:8080/uploads/avatars/42/uuid.jpg";
        when(userService.uploadAvatar(eq(USER_ID), any())).thenReturn(expectedUrl);

        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", new byte[1024]);

        mockMvc.perform(multipart("/api/v1/users/avatar")
                        .file(file)
                        .with(authentication(userAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.avatarUrl").value(expectedUrl));
    }

    // avatar upload: 未认证返回 401
    @Test
    void uploadAvatar_unauthenticated_returns401() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", new byte[1024]);

        mockMvc.perform(multipart("/api/v1/users/avatar").file(file))
                .andExpect(status().isUnauthorized());
    }
}
