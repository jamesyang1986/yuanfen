package com.yf.yuanfen.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.yf.yuanfen.IntegrationTestBase;
import com.yf.yuanfen.auth.sms.MockSmsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockSmsServiceImpl mockSmsService;

    // ── 用户名注册 ─────────────────────────────────────────────────────────────

    @Test
    void registerByUsername_success_returns201WithTokens() throws Exception {
        String body = json("username", TEST_PREFIX + "alice", "password", "Password123");

        mockMvc.perform(post("/api/v1/auth/register/username")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value(201))
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    void registerByUsername_duplicateUsername_returns409() throws Exception {
        String body = json("username", TEST_PREFIX + "bob", "password", "Password123");

        mockMvc.perform(post("/api/v1/auth/register/username")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register/username")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    void registerByUsername_shortUsername_returns400() throws Exception {
        String body = json("username", "ab", "password", "Password123");

        mockMvc.perform(post("/api/v1/auth/register/username")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void registerByUsername_weakPassword_returns400() throws Exception {
        String body = json("username", TEST_PREFIX + "carol", "password", "short");

        mockMvc.perform(post("/api/v1/auth/register/username")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest());
    }

    // ── 邮箱注册 ─────────────────────────────────────────────────────────────

    @Test
    void registerByEmail_success_returns201() throws Exception {
        String body = json("email", TEST_PREFIX + "dave@test.com", "password", "Password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register/email")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andReturn();

        // 邮箱注册后 users 表中 email 字段写入，但 username 为 null，清理需按 email
        // → 补充清理（基类只清 username LIKE it_%）
        jdbcTemplate.update("DELETE rt FROM refresh_tokens rt JOIN users u ON rt.user_id = u.id WHERE u.email = ?", TEST_PREFIX + "dave@test.com");
        jdbcTemplate.update("DELETE FROM users WHERE email = ?", TEST_PREFIX + "dave@test.com");

        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString()).path("data");
        assertThat(data.path("accessToken").asText()).isNotBlank();
    }

    @Test
    void registerByEmail_duplicate_returns409() throws Exception {
        String body = json("email", TEST_PREFIX + "dup@test.com", "password", "Password123");

        mockMvc.perform(post("/api/v1/auth/register/email")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register/email")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isConflict());

        jdbcTemplate.update("DELETE rt FROM refresh_tokens rt JOIN users u ON rt.user_id = u.id WHERE u.email = ?", TEST_PREFIX + "dup@test.com");
        jdbcTemplate.update("DELETE FROM users WHERE email = ?", TEST_PREFIX + "dup@test.com");
    }

    // ── 手机号注册 ────────────────────────────────────────────────────────────

    @Test
    void registerByPhone_success_returns201() throws Exception {
        String phone = "13800001001";

        // 1. 发送验证码
        mockMvc.perform(post("/api/v1/sms/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("phone", phone)))
            .andExpect(status().isOk());

        // 2. 从 MockSmsServiceImpl 内部 store 取出验证码
        String code = extractSmsCode(phone);

        // 3. 注册
        String body = json("phone", phone, "smsCode", code, "password", "Password123");
        mockMvc.perform(post("/api/v1/auth/register/phone")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty());

        jdbcTemplate.update("DELETE rt FROM refresh_tokens rt JOIN users u ON rt.user_id = u.id WHERE u.phone = ?", phone);
        jdbcTemplate.update("DELETE FROM users WHERE phone = ?", phone);
    }

    @Test
    void registerByPhone_wrongSmsCode_returns400() throws Exception {
        String phone = "13800001002";

        mockMvc.perform(post("/api/v1/sms/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("phone", phone)))
            .andExpect(status().isOk());

        String body = json("phone", phone, "smsCode", "000000", "password", "Password123");
        mockMvc.perform(post("/api/v1/auth/register/phone")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest());
    }

    // ── 登录 ──────────────────────────────────────────────────────────────────

    @Test
    void loginByUsername_success_returns200() throws Exception {
        registerUsername(TEST_PREFIX + "eve", "Password123");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("loginType", "USERNAME_PASSWORD",
                              "identifier", TEST_PREFIX + "eve",
                              "credential", "Password123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    void loginByUsername_wrongPassword_returns401() throws Exception {
        registerUsername(TEST_PREFIX + "frank", "Password123");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("loginType", "USERNAME_PASSWORD",
                              "identifier", TEST_PREFIX + "frank",
                              "credential", "WrongPass1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void loginByUsername_nonExistentUser_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("loginType", "USERNAME_PASSWORD",
                              "identifier", "nobody_xyz",
                              "credential", "Password123")))
            .andExpect(status().isUnauthorized());
    }

    // ── Token 刷新 & 登出 ─────────────────────────────────────────────────────

    @Test
    void refreshToken_success_returnsNewAccessToken() throws Exception {
        registerUsername(TEST_PREFIX + "grace", "Password123");

        String loginBody = json("loginType", "USERNAME_PASSWORD",
                                "identifier", TEST_PREFIX + "grace",
                                "credential", "Password123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andReturn();
        String refreshToken = extractToken(loginResult, "refreshToken");

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("refreshToken", refreshToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }

    @Test
    void logout_success_returns200() throws Exception {
        String token = registerUsername(TEST_PREFIX + "henry", "Password123");

        String loginBody = json("loginType", "USERNAME_PASSWORD",
                                "identifier", TEST_PREFIX + "henry",
                                "credential", "Password123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andReturn();
        String refreshToken = extractToken(loginResult, "refreshToken");

        mockMvc.perform(post("/api/v1/auth/logout")
                .header("Authorization", bearerHeader(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("refreshToken", refreshToken)))
            .andExpect(status().isOk());
    }

    // ── 工具方法 ──────────────────────────────────────────────────────────────

    /** 从 MockSmsServiceImpl 内部 store 中反射取出验证码 */
    @SuppressWarnings("unchecked")
    private String extractSmsCode(String phone) throws Exception {
        Field storeField = MockSmsServiceImpl.class.getDeclaredField("store");
        storeField.setAccessible(true);
        Map<String, Object> store = (Map<String, Object>) storeField.get(mockSmsService);
        Object entry = store.get(phone);
        Field codeField = entry.getClass().getDeclaredField("code");
        codeField.setAccessible(true);
        return (String) codeField.get(entry);
    }

    private String json(String... kvs) throws Exception {
        Map<String, String> map = new java.util.LinkedHashMap<>();
        for (int i = 0; i < kvs.length; i += 2) map.put(kvs[i], kvs[i + 1]);
        return objectMapper.writeValueAsString(map);
    }
}
