package com.seckill.platform.search.config;

import com.seckill.platform.search.loadbalace.GrayRule;
import org.springframework.context.annotation.Bean;

public class GrayRuleConfig {
    @Bean
    public GrayRule grayRule(){
        return new GrayRule();
    }
}
