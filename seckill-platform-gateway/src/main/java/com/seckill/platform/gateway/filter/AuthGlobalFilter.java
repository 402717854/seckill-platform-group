package com.seckill.platform.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.seckill.platform.common.constant.AuthConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 将登录用户的JWT转化成用户信息的全局过滤器
 * Created by macro on 2020/6/17.
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static Logger LOGGER = LoggerFactory.getLogger(AuthGlobalFilter.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final static String REDIS_DATABASE="seckill:system:online-token-";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String token = exchange.getRequest().getHeaders().getFirst(AuthConstant.TOKEN_HEADER);
        if (StrUtil.isEmpty(token))
        {
            token = serverHttpRequest.getQueryParams().getFirst(AuthConstant.TOKEN_HEADER);
        }
        if(StrUtil.isNotEmpty(token)){
            String realToken = token.replace(AuthConstant.TOKEN_PREFIX, "");
            if(StrUtil.isNotEmpty(realToken)){
                try {
                    String key = REDIS_DATABASE + realToken;
                    //判断缓冲中是否存在登录用户信息
                    JSONObject jsonObject = (JSONObject)redisTemplate.opsForValue().get(key);
                    if(jsonObject!=null){
                        //从token中解析用户信息并设置到Header中去
                        String userStr = jsonObject.toString();
                        LOGGER.info("AuthGlobalFilter.filter() user:{}",userStr);
                        ServerHttpRequest request = exchange.getRequest().mutate().header(AuthConstant.USER_TOKEN_HEADER, userStr).build();
                        exchange = exchange.mutate().request(request).build();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
