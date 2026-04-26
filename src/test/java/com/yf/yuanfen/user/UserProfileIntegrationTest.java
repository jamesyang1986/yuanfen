package com.yf.yuanfen.user;

import com.yf.yuanfen.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserProfileIntegrationTest extends IntegrationTestBase {

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        token = registerUsername(TEST_PREFIX + "profuser", "Password123");
    }

    // ── GET /api/v1/users/profile ─────────────────────────────────────────────

    @Test
    void getProfile_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getProfile_newUser_allFieldsNull() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile")
                .header("Authorization", bearerHeader(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.nickname").doesNotExist())
            .andExpect(jsonPath("$.data.gender").doesNotExist())
            .andExpect(jsonPath("$.data.birthDate").doesNotExist())
            .andExpect(jsonPath("$.data.avatarUrl").doesNotExist());
    }

    @Test
    void getProfile_invalidToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile")
                .header("Authorization", "Bearer invalid.token.here"))
            .andExpect(status().isUnauthorized());
    }

    // ── PUT /api/v1/users/profile ─────────────────────────────────────────────

    @Test
    void updateProfile_nickname_returns200AndPersists() throws Exception {
        String body = json("nickname", "测试昵称");

        mockMvc.perform(put("/api/v1/users/profile")
                .header("Authorization", bearerHeader(token))
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.nickname").value("测试昵称"));

        // 再次 GET 验证持久化
        mockMvc.perform(get("/api/v1/users/profile")
                .header("Authorization", bearerHeader(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.nickname").value("测试昵称"));
    }

    @Test
    void updateProfile_allFields_returns200() throws Exception {
        String body = json(
            "nickname", "小明",
            "gender", "1",
            "birthDate", "1995-06",
            "city", "北京",
            "address", "朝阳区某路"
        );

        mockMvc.perform(put("/api/v1/users/profile")
                .header("Authorization", bearerHeader(token))
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.nickname").value("小明"))
            .andExpect(jsonPath("$.data.gender").value(1))
            .andExpect(jsonPath("$.data.birthDate").value("1995-06"))
            .andExpect(jsonPath("$.data.city").value("北京"))
            .andExpect(jsonPath("$.data.address").value("朝阳区某路"));
    }

    @Test
    void updateProfile_partialUpdate_doesNotClearOtherFields() throws Exception {
        // 先设置 nickname
        mockMvc.perform(put("/api/v1/users/profile")
                .header("Authorization", bearerHeader(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("nickname", "原昵称", "city", "上海")))
            .andExpect(status().isOk());

        // 只更新 city，nickname 应保持不变
        mockMvc.perform(put("/api/v1/users/profile")
                .header("Authorization", bearerHeader(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("city", "广州")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.city").value("广州"))
            .andExpect(jsonPath("$.data.nickname").value("原昵称"));
    }

    @Test
    void updateProfile_invalidBirthDateFormat_returns400() throws Exception {
        String body = json("birthDate", "not-a-date");

        mockMvc.perform(put("/api/v1/users/profile")
                .header("Authorization", bearerHeader(token))
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void updateProfile_nicknameTooLong_returns400() throws Exception {
        String body = json("nickname", "a".repeat(33));

        mockMvc.perform(put("/api/v1/users/profile")
                .header("Authorization", bearerHeader(token))
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfile_unauthenticated_returns401() throws Exception {
        mockMvc.perform(put("/api/v1/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("nickname", "noauth")))
            .andExpect(status().isUnauthorized());
    }

    // ── POST /api/v1/users/avatar ─────────────────────────────────────────────

    @Test
    void uploadAvatar_validJpeg_returns200WithUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "avatar.jpg", "image/jpeg", new byte[1024]);

        String response = mockMvc.perform(multipart("/api/v1/users/avatar")
                .file(file)
                .header("Authorization", bearerHeader(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.avatarUrl").isNotEmpty())
            .andReturn().getResponse().getContentAsString();

        // 验证 avatarUrl 写入了 profile
        String avatarUrl = objectMapper.readTree(response).path("data").path("avatarUrl").asText();
        assertThat(avatarUrl).contains("/uploads/avatars/");

        mockMvc.perform(get("/api/v1/users/profile")
                .header("Authorization", bearerHeader(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.avatarUrl").value(avatarUrl));
    }

    @Test
    void uploadAvatar_validPng_returns200() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "avatar.png", "image/png", new byte[512]);

        mockMvc.perform(multipart("/api/v1/users/avatar")
                .file(file)
                .header("Authorization", bearerHeader(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.avatarUrl").isNotEmpty());
    }

    @Test
    void uploadAvatar_unsupportedType_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "doc.pdf", "application/pdf", new byte[1024]);

        mockMvc.perform(multipart("/api/v1/users/avatar")
                .file(file)
                .header("Authorization", bearerHeader(token)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void uploadAvatar_fileTooLarge_returns400() throws Exception {
        // 6MB，超过 5MB 限制
        byte[] bigContent = new byte[6 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
            "file", "big.jpg", "image/jpeg", bigContent);

        mockMvc.perform(multipart("/api/v1/users/avatar")
                .file(file)
                .header("Authorization", bearerHeader(token)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void uploadAvatar_unauthenticated_returns401() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "avatar.jpg", "image/jpeg", new byte[512]);

        mockMvc.perform(multipart("/api/v1/users/avatar").file(file))
            .andExpect(status().isUnauthorized());
    }

    // ── 工具方法 ──────────────────────────────────────────────────────────────

    private String json(String... kvs) throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < kvs.length; i += 2) {
            String v = kvs[i + 1];
            try { map.put(kvs[i], Integer.parseInt(v)); }
            catch (NumberFormatException e) { map.put(kvs[i], v); }
        }
        return objectMapper.writeValueAsString(map);
    }
}
