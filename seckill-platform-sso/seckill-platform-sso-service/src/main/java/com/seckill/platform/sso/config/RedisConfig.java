package com.seckill.platform.sso.config;

import com.seckill.platform.common.config.BaseRedisConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Redis相关配置
 *
 * @author wys
 * @date 2022/09/02
 */
@EnableCaching
@Configuration
public class RedisConfig extends BaseRedisConfig {

}
