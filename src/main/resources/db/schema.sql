-- MySQL 8.0 compatible DDL
-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`      VARCHAR(32)     CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户名',
    `email`         VARCHAR(128)    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
    `phone`         VARCHAR(20)     CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
    `password_hash` VARCHAR(256)    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '密码哈希(BCrypt)',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `nickname`      VARCHAR(32)     CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '昵称',
    `gender`        TINYINT         DEFAULT NULL COMMENT '性别(0:未知,1:男,2:女,3:其他)',
    `birth_date`    DATE            DEFAULT NULL COMMENT '出生年月(精确到月,存每月1日)',
    `city`          VARCHAR(64)     CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '所在城市',
    `address`       VARCHAR(256)    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '居住地址',
    `avatar_url`    VARCHAR(512)    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像URL',
    `last_login_at` DATETIME        DEFAULT NULL COMMENT '最后登录时间',
    `occupation`    VARCHAR(64)     CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '职业',
    `bio`           TEXT            CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '自我介绍',
    `partner_tags`  VARCHAR(512)    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '择偶标签(逗号分隔)',
    `wechat_id`     VARCHAR(64)     CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '微信号',
    `qq_number`     VARCHAR(20)     CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'QQ号',
    `wechat_openid` VARCHAR(64)     CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '微信OpenID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_phone`        (`phone`),
    UNIQUE KEY `uk_users_email`        (`email`),
    UNIQUE KEY `uk_users_username`     (`username`),
    UNIQUE KEY `uk_users_wechat_openid`(`wechat_openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- Refresh Token 表
CREATE TABLE IF NOT EXISTS `refresh_tokens` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id`    BIGINT       NOT NULL COMMENT '用户ID',
    `token`      VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Refresh Token',
    `expires_at` DATETIME     NOT NULL COMMENT '过期时间',
    `revoked`    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否已撤销(0:否,1:是)',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refresh_tokens_token` (`token`),
    KEY `idx_refresh_tokens_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Refresh Token表';
