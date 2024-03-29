package com.seckill.platform.sso.config;

import com.seckill.platform.common.config.BaseSwaggerConfig;
import com.seckill.platform.common.domain.SwaggerProperties;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档相关配置
 * Created by macro on 2018/4/26.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.macro.mall.portal.controller")
                .title("seckill-platform-sso前台系统")
                .description("sso前台相关接口文档")
                .contactName("wys")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }

//    @Bean
//    public BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
//        return generateBeanPostProcessor();
//    }
}
