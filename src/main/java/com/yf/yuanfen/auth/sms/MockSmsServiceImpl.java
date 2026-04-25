package com.yf.yuanfen.auth.sms;

import com.yf.yuanfen.common.exception.BizException;
import com.yf.yuanfen.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MockSmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(MockSmsServiceImpl.class);

    private static final long CODE_EXPIRY_MS = 5 * 60 * 1000L;    // 5分钟
    private static final long SEND_INTERVAL_MS = 60 * 1000L;       // 60秒

    private final Map<String, CodeEntry> store = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @Override
    public void sendCode(String phone) {
        CodeEntry existing = store.get(phone);
        if (existing != null && Instant.now().toEpochMilli() - existing.sentAt < SEND_INTERVAL_MS) {
            throw new BizException(ErrorCode.SMS_SEND_TOO_FREQUENT);
        }
        String code = String.format("%06d", random.nextInt(1_000_000));
        store.put(phone, new CodeEntry(code, Instant.now().toEpochMilli()));
        log.info("[MockSMS] phone={} code={}", phone, code);
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        CodeEntry entry = store.get(phone);
        if (entry == null) {
            return false;
        }
        if (Instant.now().toEpochMilli() - entry.sentAt > CODE_EXPIRY_MS) {
            store.remove(phone);
            throw new BizException(ErrorCode.SMS_CODE_EXPIRED);
        }
        if (!entry.code.equals(code)) {
            return false;
        }
        store.remove(phone); // one-time use
        return true;
    }

    private static class CodeEntry {
        final String code;
        final long sentAt;

        CodeEntry(String code, long sentAt) {
            this.code = code;
            this.sentAt = sentAt;
        }
    }
}
