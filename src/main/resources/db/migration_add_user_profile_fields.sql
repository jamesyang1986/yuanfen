-- 对已有数据库实例执行此脚本以增加用户 profile 字段
-- 新部署环境直接使用 schema.sql，无需执行本文件
ALTER TABLE `users`
    ADD COLUMN IF NOT EXISTS `nickname`   VARCHAR(32)  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
    ADD COLUMN IF NOT EXISTS `gender`     TINYINT      DEFAULT NULL COMMENT '性别(0:未知,1:男,2:女,3:其他)',
    ADD COLUMN IF NOT EXISTS `birth_date` DATE         DEFAULT NULL COMMENT '出生年月(精确到月,存每月1日)',
    ADD COLUMN IF NOT EXISTS `city`       VARCHAR(64)  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所在城市',
    ADD COLUMN IF NOT EXISTS `address`    VARCHAR(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '居住地址',
    ADD COLUMN IF NOT EXISTS `avatar_url` VARCHAR(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL';
