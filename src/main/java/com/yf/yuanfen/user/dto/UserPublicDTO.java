package com.yf.yuanfen.user.dto;

import java.util.List;

public class UserPublicDTO {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private String city;
    private String birthDate;
    private Integer age;
    private String occupation;
    private String bio;
    private List<String> partnerTags;
    private String wechatId;
    private String qqNumber;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public List<String> getPartnerTags() { return partnerTags; }
    public void setPartnerTags(List<String> partnerTags) { this.partnerTags = partnerTags; }

    public String getWechatId() { return wechatId; }
    public void setWechatId(String wechatId) { this.wechatId = wechatId; }

    public String getQqNumber() { return qqNumber; }
    public void setQqNumber(String qqNumber) { this.qqNumber = qqNumber; }
}
