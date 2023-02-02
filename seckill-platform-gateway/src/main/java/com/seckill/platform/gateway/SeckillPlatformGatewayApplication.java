package com.seckill.platform.gateway;

import com.seckill.platform.gateway.config.GrayRuleConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;

/**
 * @author wangys10
 * @date 2022/8/31 11:14
 */
@RibbonClients(value ={
        //只对指定服务进行灰度发布
        @RibbonClient(value = "seckill-platform-search",configuration = GrayRuleConfig.class)
} )
@SpringBootApplication
public class SeckillPlatformGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillPlatformGatewayApplication.class, args);
    }
}
