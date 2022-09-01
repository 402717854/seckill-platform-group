package com.seckill.platform.sso;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author wangys10
 * @date 2022/8/31 22:29
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = {"com.seckill.platform.sso.mapper","com.seckill.platform.sso.dao"})
public class SeckillPlatformSsoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillPlatformSsoServiceApplication.class, args);
    }
}
