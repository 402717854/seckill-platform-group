package com.seckill.platform.search;

import com.seckill.platform.search.canal.CanalSink;
import com.seckill.platform.search.config.GrayRuleConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * @author wangys10
 * @date 2022/9/10 9:07
 */
@RibbonClients(value ={
        //只对指定服务进行灰度发布
        @RibbonClient(value = "seckill-platform-goods",configuration = GrayRuleConfig.class)
} )
@EnableFeignClients(basePackages = {"com.seckill.platform.goods"})
@EnableDiscoveryClient
@SpringBootApplication
@EnableBinding(CanalSink.class)
public class SeckillPlatformSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillPlatformSearchApplication.class, args);
    }
}
