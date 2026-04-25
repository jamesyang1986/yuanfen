package com.yf.yuanfen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yf.yuanfen.**.mapper")
public class YuanfenApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuanfenApplication.class, args);
    }

}
