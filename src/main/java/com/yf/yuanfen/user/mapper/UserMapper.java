package com.yf.yuanfen.user.mapper;

import com.yf.yuanfen.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User selectByPhone(@Param("phone") String phone);

    User selectByEmail(@Param("email") String email);

    User selectByUsername(@Param("username") String username);

    void insert(User user);

    boolean existsByPhone(@Param("phone") String phone);

    boolean existsByEmail(@Param("email") String email);

    boolean existsByUsername(@Param("username") String username);

    User selectProfileById(@Param("id") Long id);

    void updateProfile(User user);
}
