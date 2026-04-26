-- 迁移脚本：为 users 表新增扩展资料字段
ALTER TABLE `users`
    ADD COLUMN `occupation`   VARCHAR(64)  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '职业',
    ADD COLUMN `bio`          TEXT         CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '自我介绍',
    ADD COLUMN `partner_tags` VARCHAR(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '择偶标签(逗号分隔)';
