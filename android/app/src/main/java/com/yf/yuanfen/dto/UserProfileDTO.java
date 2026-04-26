package com.yf.yuanfen.dto;

import java.util.List;

public class UserProfileDTO {
    private String nickname;
    private Integer gender;
    private String birthDate;
    private String city;
    private String address;
    private String avatarUrl;
    private String occupation;
    private String bio;
    private Integer age;
    private List<String> partnerTags;
    private String wechatId;
    private String qqNumber;

    public String getNickname() { return nickname; }
    public Integer getGender() { return gender; }
    public String getBirthDate() { return birthDate; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getOccupation() { return occupation; }
    public String getBio() { return bio; }
    public Integer getAge() { return age; }
    public List<String> getPartnerTags() { return partnerTags; }
    public String getWechatId() { return wechatId; }
    public String getQqNumber() { return qqNumber; }
}
