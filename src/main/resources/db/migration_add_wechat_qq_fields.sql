-- 新增微信号、QQ号、微信OpenID字段
ALTER TABLE `users`
    ADD COLUMN `wechat_id`     VARCHAR(64)  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '微信号' AFTER `partner_tags`,
    ADD COLUMN `qq_number`     VARCHAR(20)  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'QQ号' AFTER `wechat_id`,
    ADD COLUMN `wechat_openid` VARCHAR(64)  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '微信OpenID' AFTER `qq_number`,
    ADD UNIQUE KEY `uk_users_wechat_openid` (`wechat_openid`);
