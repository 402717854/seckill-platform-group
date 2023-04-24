package com.seckill.platform.drools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SystemDroolsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemDroolsApplication.class, args);
        System.out.println("***********规则引擎模块启动成功***********");
    }

}
