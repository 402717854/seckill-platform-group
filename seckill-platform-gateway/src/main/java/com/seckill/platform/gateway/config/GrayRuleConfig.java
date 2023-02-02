package com.seckill.platform.gateway.config;

import com.seckill.platform.gateway.loadbalance.GrayRule;
import org.springframework.context.annotation.Bean;

public class GrayRuleConfig {
    @Bean
    public GrayRule grayRule(){
        return new GrayRule();
    }
}
