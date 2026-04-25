package com.yf.yuanfen.common.exception;

public enum ErrorCode {

    PHONE_ALREADY_EXISTS(409, "手机号已被注册"),
    EMAIL_ALREADY_EXISTS(409, "邮箱已被注册"),
    USERNAME_ALREADY_EXISTS(409, "用户名已被注册"),

    INVALID_SMS_CODE(400, "验证码无效或已过期"),
    INVALID_PHONE_FORMAT(400, "手机号格式错误"),
    INVALID_PASSWORD_FORMAT(400, "密码格式错误"),
    INVALID_EMAIL_FORMAT(400, "邮箱格式错误"),
    INVALID_USERNAME_FORMAT(400, "用户名格式错误"),
    MISSING_REQUIRED_FIELD(400, "缺少必要字段"),

    INVALID_CREDENTIALS(401, "账号或密码错误"),
    USER_NOT_FOUND(401, "用户不存在"),
    TOKEN_EXPIRED(401, "Token已过期"),
    INVALID_TOKEN(401, "Token无效"),
    UNAUTHORIZED(401, "未授权"),
    REFRESH_TOKEN_EXPIRED(401, "Refresh Token已过期"),
    INVALID_REFRESH_TOKEN(401, "Refresh Token无效"),
    REFRESH_TOKEN_REVOKED(401, "Refresh Token已撤销"),

    SMS_CODE_EXPIRED(400, "验证码已过期"),
    SMS_SEND_TOO_FREQUENT(429, "短信发送过于频繁"),

    INTERNAL_ERROR(500, "服务器内部错误");

    private final int httpStatus;
    private final String message;

    ErrorCode(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
