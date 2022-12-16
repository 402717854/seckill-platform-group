package com.seckill.platform.search;

import com.seckill.platform.search.canal.CanalSink;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * @author wangys10
 * @date 2022/9/10 9:07
 */
@EnableFeignClients(basePackages = {"com.seckill.platform.goods"})
@EnableDiscoveryClient
@SpringBootApplication
@EnableBinding(CanalSink.class)
public class SeckillPlatformSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillPlatformSearchApplication.class, args);
    }
}
