package com.yf.yuanfen.auth.wechat;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "wechat.mock", havingValue = "true")
public class WechatMockServiceImpl implements WechatService {

    private static final String MOCK_OPENID = "mock_openid_test";

    @Override
    public String buildAuthUrl(String redirectUri, String state) {
        // Mock 模式：直接构造指向本地回调的 URL，携带固定 code
        return redirectUri + "?code=mock_code&state=" + state;
    }

    @Override
    public WechatUserInfo exchangeCode(String code) {
        return new WechatUserInfo(MOCK_OPENID, "微信测试用户", null);
    }
}
