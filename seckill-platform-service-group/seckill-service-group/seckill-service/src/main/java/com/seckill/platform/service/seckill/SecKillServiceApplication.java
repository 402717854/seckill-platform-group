package com.seckill.platform.service.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 启动类
 * @author lenovo
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SecKillServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecKillServiceApplication.class, args);
    }
}
