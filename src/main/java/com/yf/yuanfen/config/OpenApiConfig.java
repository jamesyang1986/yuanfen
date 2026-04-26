package com.yf.yuanfen.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("缘分 App API")
                        .description("缘分交友平台后端接口文档。\n\n" +
                                "**认证说明**：受保护接口需在请求头携带 JWT：`Authorization: Bearer <token>`\n\n" +
                                "点击右上角 **Authorize** 按钮输入 Token 后，可直接在页面调试受保护接口。")
                        .version("1.0.0")
                        .contact(new Contact().name("yuanfen team")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("填写登录/注册接口返回的 accessToken")));
    }
}
