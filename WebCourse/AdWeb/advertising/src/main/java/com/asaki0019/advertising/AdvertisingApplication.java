package com.asaki0019.advertising;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.asaki0019.advertising.mapper")
public class AdvertisingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvertisingApplication.class, args);
    }

}
