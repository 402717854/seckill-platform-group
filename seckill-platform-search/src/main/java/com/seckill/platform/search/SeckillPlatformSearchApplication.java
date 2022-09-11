package com.seckill.platform.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author wangys10
 * @date 2022/9/10 9:07
 */
@EnableFeignClients(basePackages = {"com.seckill.platform.goods"})
@EnableDiscoveryClient
@SpringBootApplication
public class SeckillPlatformSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillPlatformSearchApplication.class, args);
    }
}
