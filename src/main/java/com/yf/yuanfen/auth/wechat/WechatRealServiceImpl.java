package com.yf.yuanfen.auth.wechat;

import com.yf.yuanfen.common.exception.BizException;
import com.yf.yuanfen.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "wechat.mock", havingValue = "false", matchIfMissing = true)
public class WechatRealServiceImpl implements WechatService {

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String buildAuthUrl(String redirectUri, String state) {
        String encoded = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        return "https://open.weixin.qq.com/connect/oauth2/authorize"
                + "?appid=" + appid
                + "&redirect_uri=" + encoded
                + "&response_type=code"
                + "&scope=snsapi_userinfo"
                + "&state=" + state
                + "#wechat_redirect";
    }

    @Override
    @SuppressWarnings("unchecked")
    public WechatUserInfo exchangeCode(String code) {
        String tokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token"
                + "?appid=" + appid
                + "&secret=" + secret
                + "&code=" + code
                + "&grant_type=authorization_code";

        Map<String, Object> tokenResp = restTemplate.getForObject(tokenUrl, Map.class);
        if (tokenResp == null || tokenResp.containsKey("errcode")) {
            throw new BizException(ErrorCode.WECHAT_CODE_INVALID);
        }

        String accessToken = (String) tokenResp.get("access_token");
        String openid = (String) tokenResp.get("openid");

        String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo"
                + "?access_token=" + accessToken
                + "&openid=" + openid
                + "&lang=zh_CN";

        Map<String, Object> userResp = restTemplate.getForObject(userInfoUrl, Map.class);
        if (userResp == null || userResp.containsKey("errcode")) {
            throw new BizException(ErrorCode.WECHAT_CODE_INVALID);
        }

        String nickname = (String) userResp.get("nickname");
        String headimgurl = (String) userResp.get("headimgurl");
        return new WechatUserInfo(openid, nickname, headimgurl);
    }
}
