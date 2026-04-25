#!/usr/bin/env bash
# 初始化 yuanfen 数据库（MySQL 8.0）
# 用法: ./scripts/init-db.sh [host] [port] [user] [password]
# 默认值从 application.properties 读取

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SQL_DIR="$SCRIPT_DIR/../src/main/resources/db"

HOST="${1:-localhost}"
PORT="${2:-3306}"
USER="${3:-root}"
PASS="${4:-123qweasd}"

MYSQL="mysql -h$HOST -P$PORT -u$USER -p$PASS --default-character-set=utf8mb4"

echo "==> 连接 MySQL $HOST:$PORT ..."
$MYSQL -e "SELECT VERSION();" 2>/dev/null | grep -v "Using a password"

echo "==> 创建数据库 yuanfen（若不存在）..."
$MYSQL -e "CREATE DATABASE IF NOT EXISTS \`yuanfen\`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;" 2>/dev/null

echo "==> 执行 schema.sql ..."
$MYSQL yuanfen < "$SQL_DIR/schema.sql" 2>/dev/null

echo "==> 完成！当前表结构："
$MYSQL yuanfen -e "SHOW TABLES;" 2>/dev/null
