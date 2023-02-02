package com.seckill.platform.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author wangys10
 * @date 2022/8/31 9:46
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.seckill.platform.admin","com.seckill.platform.common"})
@MapperScan(basePackages = {"com.seckill.platform.admin.mapper","com.seckill.platform.admin.dao"})
public class SeckillPlatformAdminServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillPlatformAdminServiceApplication.class, args);
    }
}
