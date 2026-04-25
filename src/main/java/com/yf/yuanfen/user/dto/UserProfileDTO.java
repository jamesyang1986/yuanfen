package com.yf.yuanfen.user.dto;

import com.yf.yuanfen.user.entity.Gender;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class UserProfileDTO {

    @Size(max = 32, message = "昵称长度不能超过32个字符")
    private String nickname;

    private Integer gender;

    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "出生年月格式应为 YYYY-MM")
    private String birthDate;

    @Size(max = 64, message = "城市长度不能超过64个字符")
    private String city;

    @Size(max = 256, message = "地址长度不能超过256个字符")
    private String address;

    @Size(max = 512, message = "头像URL长度不能超过512个字符")
    private String avatarUrl;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    /** 将 YYYY-MM 格式的 birthDate 转为 LocalDate（每月 1 日），null 时返回 null */
    public LocalDate toBirthLocalDate() {
        if (birthDate == null) return null;
        return LocalDate.parse(birthDate + "-01");
    }

    /** 从 LocalDate 生成 YYYY-MM 格式字符串用于响应，null 时返回 null */
    public static String fromBirthLocalDate(LocalDate date) {
        if (date == null) return null;
        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
    }
}
