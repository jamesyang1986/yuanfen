package com.yf.yuanfen.user.mapper;

import com.yf.yuanfen.user.entity.User;
import java.util.List;
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

    void updateAvatarUrl(@Param("id") Long id, @Param("avatarUrl") String avatarUrl);

    List<User> listUsers(@Param("offset") int offset, @Param("size") int size);

    int countUsers();

    void updateLastLoginAt(@Param("id") Long id);
}
