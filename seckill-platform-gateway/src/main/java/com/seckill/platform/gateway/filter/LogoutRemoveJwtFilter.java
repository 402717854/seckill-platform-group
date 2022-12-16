package com.seckill.platform.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nimbusds.jose.JWSObject;
import com.seckill.platform.common.constant.AuthConstant;
import com.seckill.platform.common.domain.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

/**
 * 判断token在缓存中是否失效，失效删除head值
 * Created by macro on 2022/09/13.
 */
@Component
public class LogoutRemoveJwtFilter implements WebFilter {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final static String REDIS_DATABASE="mall";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
        if(StrUtil.isNotEmpty(token)){
            String realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
            if(StrUtil.isNotEmpty(realToken)){
                try {
                    JWSObject jwsObject = JWSObject.parse(realToken);
                    String userStr = jwsObject.getPayload().toString();
                    UserDto userDto = JSONUtil.toBean(userStr, UserDto.class);
                    String key = REDIS_DATABASE + ":" + userDto.getClientId() + ":" + userDto.getId();
                    //判断缓冲中是否存在登录用户信息
                    JSONObject jsonObject = (JSONObject)redisTemplate.opsForValue().get(key);
                    //缓存中不存在的登录信息移除JWT请求头
                    if(jsonObject==null){
                        request = exchange.getRequest().mutate().header(AuthConstant.JWT_TOKEN_HEADER, "").build();
                        exchange = exchange.mutate().request(request).build();
                        return chain.filter(exchange);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return chain.filter(exchange);
    }
}
