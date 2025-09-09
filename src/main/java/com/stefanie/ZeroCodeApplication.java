package com.stefanie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.stefanie.mapper")
public class ZeroCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZeroCodeApplication.class, args);
    }

}
