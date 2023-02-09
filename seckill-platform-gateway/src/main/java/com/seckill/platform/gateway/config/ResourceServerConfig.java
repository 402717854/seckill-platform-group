package com.seckill.platform.gateway.config;

import cn.hutool.core.util.ArrayUtil;
import com.seckill.platform.gateway.authorization.AuthorizationManager;
import com.seckill.platform.gateway.component.RestAuthenticationEntryPoint;
import com.seckill.platform.gateway.component.RestfulAccessDeniedHandler;
import com.seckill.platform.gateway.filter.IgnoreUrlsRemoveTokenFilter;
import com.seckill.platform.gateway.filter.VerifyTokenFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 资源服务器配置
 * Created by macro on 2020/6/19.
 * 由于 Gateway 使用的是 WebFlux，所以需要使用 @EnableWebFluxSecurity注解开启
 */
@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {
    private final AuthorizationManager authorizationManager;
    private final IgnoreUrlsConfig ignoreUrlsConfig;
    private final RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final IgnoreUrlsRemoveTokenFilter ignoreUrlsRemoveTokenFilter;
    private final VerifyTokenFilter verifyTokenFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        //对白名单路径，直接移除JWT请求头
        http.addFilterBefore(ignoreUrlsRemoveTokenFilter,SecurityWebFiltersOrder.HTTP_BASIC);
        http.addFilterBefore(verifyTokenFilter,SecurityWebFiltersOrder.FORM_LOGIN);
        http.authorizeExchange()
                .pathMatchers(ArrayUtil.toArray(ignoreUrlsConfig.getUrls(),String.class)).permitAll()//白名单配置
                .anyExchange().access(authorizationManager)//鉴权管理器配置
                .and()
                // 授权异常
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)//处理未授权
                .authenticationEntryPoint(restAuthenticationEntryPoint)//处理未认证
                .and().csrf().disable();
        return http.build();
    }
}
