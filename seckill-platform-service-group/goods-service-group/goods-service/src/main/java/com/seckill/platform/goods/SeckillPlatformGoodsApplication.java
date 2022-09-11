package com.seckill.platform.goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author wangys10
 * @date 2022/9/10 10:06
 */
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = {"com.seckill.platform.goods.domain.dao"})
public class SeckillPlatformGoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillPlatformGoodsApplication.class, args);
    }
}
