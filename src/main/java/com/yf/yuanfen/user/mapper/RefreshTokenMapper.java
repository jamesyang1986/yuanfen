package com.yf.yuanfen.user.mapper;

import com.yf.yuanfen.user.entity.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenMapper {

    void insert(RefreshToken refreshToken);

    RefreshToken selectByToken(@Param("token") String token);

    void deleteByUserId(@Param("userId") Long userId);

    void revokeByToken(@Param("token") String token);
}
