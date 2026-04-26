package com.yf.yuanfen.dto;

import java.util.List;

public class UserPublicDTO {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private String city;
    private Integer age;
    private String occupation;
    private String bio;
    private List<String> partnerTags;
    private String wechatId;
    private String qqNumber;

    public Long getId() { return id; }
    public String getNickname() { return nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getCity() { return city; }
    public Integer getAge() { return age; }
    public String getOccupation() { return occupation; }
    public String getBio() { return bio; }
    public List<String> getPartnerTags() { return partnerTags; }
    public String getWechatId() { return wechatId; }
    public String getQqNumber() { return qqNumber; }
}
