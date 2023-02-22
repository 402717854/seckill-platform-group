package com.seckill.platform.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.seckill.framework.redisson.util.RedissonUtils;
import com.seckill.platform.common.api.CommonResult;
import com.seckill.platform.common.constant.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * 验证TOKEN
 * Created by macro on 2020/7/24.
 */
@Component
@Slf4j
public class VerifyTokenFilter implements WebFilter , Ordered {

    private final static String REDIS_DATABASE="seckill:system:online-token-";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String token = exchange.getRequest().getHeaders().getFirst(AuthConstant.TOKEN_HEADER);
        if (StrUtil.isBlank(token))
        {
            token = serverHttpRequest.getQueryParams().getFirst(AuthConstant.TOKEN_HEADER);
        }
        if(StrUtil.isNotBlank(token)){
            String realToken = token.replace(AuthConstant.TOKEN_PREFIX, "");
            if(StrUtil.isNotEmpty(realToken)){
                try {
                    String key = REDIS_DATABASE + realToken;
                    //判断缓冲中是否存在登录用户信息
                    Object object = RedissonUtils.getRBucket(key).get();
                    //缓存中不存在的登录信息
                    if(object==null){
                        response.setStatusCode(HttpStatus.OK);
                        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                        response.getHeaders().set("Access-Control-Allow-Origin","*");
                        response.getHeaders().set("Cache-Control","no-cache");
                        String body= JSONUtil.toJsonStr(CommonResult.unauthorized(null));
                        DataBuffer buffer =  response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
                        return response.writeWith(Mono.just(buffer));
                    }
                } catch (Exception e) {
                    log.error("全局过滤器:{}出现异常:{}",VerifyTokenFilter.class.getName(),e);
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    response.getHeaders().set("Access-Control-Allow-Origin","*");
                    response.getHeaders().set("Cache-Control","no-cache");
                    String body= JSONUtil.toJsonStr(CommonResult.failed("系统出错"));
                    DataBuffer buffer =  response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
                    return response.writeWith(Mono.just(buffer));
                }
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
