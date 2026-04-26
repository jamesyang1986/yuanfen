package com.yf.yuanfen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    /** 所有集成测试用户使用 it_ 前缀，测后统一清理 */
    protected static final String TEST_PREFIX = "it_";

    @AfterEach
    void cleanupTestData() {
        jdbcTemplate.update(
            "DELETE rt FROM refresh_tokens rt " +
            "JOIN users u ON rt.user_id = u.id " +
            "WHERE u.username LIKE ?", TEST_PREFIX + "%");
        jdbcTemplate.update(
            "DELETE FROM users WHERE username LIKE ?", TEST_PREFIX + "%");
    }

    /** 注册用户名账号，返回 accessToken */
    protected String registerUsername(String username, String password) throws Exception {
        String body = objectMapper.writeValueAsString(
            new java.util.HashMap<String, String>() {{
                put("username", username);
                put("password", password);
            }});

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andReturn();

        return extractToken(result, "accessToken");
    }

    /** 用户名登录，返回 accessToken */
    protected String loginUsername(String username, String password) throws Exception {
        String body = objectMapper.writeValueAsString(
            new java.util.HashMap<String, String>() {{
                put("loginType", "USERNAME_PASSWORD");
                put("identifier", username);
                put("credential", password);
            }});

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();

        return extractToken(result, "accessToken");
    }

    protected String extractToken(MvcResult result, String field) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("data").path(field).asText();
    }

    protected String bearerHeader(String token) {
        return "Bearer " + token;
    }
}
