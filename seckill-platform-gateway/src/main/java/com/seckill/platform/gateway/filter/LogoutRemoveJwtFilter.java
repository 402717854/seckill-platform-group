package com.seckill.platform.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.seckill.platform.common.constant.AuthConstant;
import com.seckill.platform.gateway.config.IgnoreUrlsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

/**
 * 判断token在缓存中是否失效，失效删除head值
 * Created by macro on 2022/09/13.
 */
@Component
public class LogoutRemoveJwtFilter implements WebFilter {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final static String REDIS_DATABASE="mall";
    private final static String REDIS_KEY_MEMBER="ums:member";
    private final static String REDIS_KEY_ADMIN="ums:admin";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
        if(StrUtil.isNotEmpty(token)){
            String realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
            if(StrUtil.isNotEmpty(realToken)){
                URI uri = request.getURI();
                PathMatcher pathMatcher = new AntPathMatcher();
                String key = realToken;
                //运营端用户
                if(pathMatcher.match(AuthConstant.ADMIN_URL_PATTERN, uri.getPath())){
                     key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + realToken;
                }else{
                    //商城会员
                     key = REDIS_DATABASE + ":" + REDIS_KEY_MEMBER + ":" + realToken;
                }
                //判断缓冲中是否存在登录用户信息
                JSONObject jsonObject = (JSONObject)redisTemplate.opsForValue().get(key);
                //缓存中不存在的登录信息移除JWT请求头
                if(jsonObject==null){
                    request = exchange.getRequest().mutate().header(AuthConstant.JWT_TOKEN_HEADER, "").build();
                    exchange = exchange.mutate().request(request).build();
                    return chain.filter(exchange);
                }
            }
        }
        return chain.filter(exchange);
    }
}
