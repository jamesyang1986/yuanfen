package com.yf.yuanfen.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "用户个人资料")
public class UserProfileDTO {

    @Schema(description = "昵称（最长 32 个字符）", example = "小明", nullable = true)
    @Size(max = 32, message = "昵称长度不能超过32个字符")
    private String nickname;

    @Schema(description = "性别（0=未知, 1=男, 2=女, 3=其他）", example = "1", nullable = true)
    private Integer gender;

    @Schema(description = "出生年月（格式 YYYY-MM）", example = "1995-06", nullable = true)
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "出生年月格式应为 YYYY-MM")
    private String birthDate;

    @Schema(description = "所在城市（最长 64 个字符）", example = "北京", nullable = true)
    @Size(max = 64, message = "城市长度不能超过64个字符")
    private String city;

    @Schema(description = "居住地址（最长 256 个字符）", example = "朝阳区某路", nullable = true)
    @Size(max = 256, message = "地址长度不能超过256个字符")
    private String address;

    @Schema(description = "头像 URL（最长 512 个字符，可通过 POST /avatar 接口上传更新）",
            example = "http://localhost:8080/uploads/avatars/1/uuid.jpg", nullable = true)
    @Size(max = 512, message = "头像URL长度不能超过512个字符")
    private String avatarUrl;

    @Schema(description = "职业（最长 64 个字符）", example = "软件工程师", nullable = true)
    @Size(max = 64, message = "职业长度不能超过64个字符")
    private String occupation;

    @Schema(description = "自我介绍（最长 500 字）", nullable = true)
    @Size(max = 500, message = "自我介绍不能超过500个字符")
    private String bio;

    @Schema(description = "择偶标签列表（最多5个）", nullable = true)
    private List<String> partnerTags;

    @Schema(description = "年龄（由 birthDate 自动计算，只读）", nullable = true)
    private Integer age;

    @Schema(description = "微信号（最长 64 个字符）", nullable = true)
    @Size(max = 64, message = "微信号长度不能超过64个字符")
    private String wechatId;

    @Schema(description = "QQ 号（最长 20 个字符）", nullable = true)
    @Size(max = 20, message = "QQ号长度不能超过20个字符")
    private String qqNumber;

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

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public List<String> getPartnerTags() { return partnerTags; }
    public void setPartnerTags(List<String> partnerTags) { this.partnerTags = partnerTags; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getWechatId() { return wechatId; }
    public void setWechatId(String wechatId) { this.wechatId = wechatId; }

    public String getQqNumber() { return qqNumber; }
    public void setQqNumber(String qqNumber) { this.qqNumber = qqNumber; }

    public LocalDate toBirthLocalDate() {
        if (birthDate == null) return null;
        return LocalDate.parse(birthDate + "-01");
    }

    public static String fromBirthLocalDate(LocalDate date) {
        if (date == null) return null;
        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
    }
}
