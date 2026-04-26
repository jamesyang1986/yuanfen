package com.yf.yuanfen.auth.sms;

import com.yf.yuanfen.common.exception.BizException;
import com.yf.yuanfen.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MockSmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(MockSmsServiceImpl.class);
    private static final String MOCK_CODE = "123456";
    private static final long CODE_TTL_SECONDS = 300L;   // 5分钟
    private static final long LOCK_TTL_SECONDS = 60L;    // 60秒发送间隔

    private final StringRedisTemplate redis;

    public MockSmsServiceImpl(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void sendCode(String phone) {
        String lockKey = "sms:lock:" + phone;
        if (Boolean.TRUE.equals(redis.hasKey(lockKey))) {
            throw new BizException(ErrorCode.SMS_SEND_TOO_FREQUENT);
        }
        redis.opsForValue().set("sms:code:" + phone, MOCK_CODE, CODE_TTL_SECONDS, TimeUnit.SECONDS);
        redis.opsForValue().set(lockKey, "1", LOCK_TTL_SECONDS, TimeUnit.SECONDS);
        log.info("[MockSMS] phone={} code={}", phone, MOCK_CODE);
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        String codeKey = "sms:code:" + phone;
        String stored = redis.opsForValue().get(codeKey);
        if (stored == null) {
            throw new BizException(ErrorCode.SMS_CODE_EXPIRED);
        }
        if (!stored.equals(code)) {
            return false;
        }
        redis.delete(codeKey);
        return true;
    }
}
