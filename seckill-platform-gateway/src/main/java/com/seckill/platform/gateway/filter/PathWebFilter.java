package com.seckill.platform.gateway.filter;


import cn.hutool.json.JSONUtil;
import com.seckill.platform.common.api.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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

@Component
@Slf4j
public class PathWebFilter implements WebFilter,Ordered{


    @Value("${spring.api.prefix}")
    private String apiPrefix;

//    /**
//     * 添加统一前缀，供mvc使用
//     */
//    @Bean
//    @ConditionalOnProperty("spring.api.prefix")
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public WebFilter contextPathWebFilter() {
//        return (exchange, chain) -> addApiPrefixFilter(exchange, chain);
//    }
//
//    private Mono<Void> addApiPrefixFilter(ServerWebExchange exchange, WebFilterChain chain) {
//        //统一添加前缀，并且校验请求是否前缀开头
//        ServerHttpRequest request = exchange.getRequest();
//        ServerHttpResponse response = exchange.getResponse();
//
//        String requestMethod = request.getMethodValue();
//        String requestPath = request.getURI().getPath();
//        if (requestPath.startsWith(apiPrefix)) {
//            ServerHttpRequest newRequest = request.mutate().contextPath(apiPrefix).build();
//            ServerHttpRequest newRequest2 = newRequest.mutate().contextPath(StringUtils.EMPTY).build();
//            ServerWebExchange newExChange = exchange.mutate().request(newRequest2).build();
//            return chain.filter(newExChange).then(
//                    Mono.fromRunnable(() -> {
//                        // 参数
//                        int value = response.getStatusCode().value();
//                        log.info("[Gateway] <=== {} {}: {}", value, requestMethod, requestPath);
//                    })
//            );
////            ServerWebExchange newExChange = exchange.mutate().request(newRequest).build();
////            return chain.filter(newExChange);
//        } else {
//            log.error("路径有误:{}",requestPath);
//            response.setStatusCode(HttpStatus.NOT_FOUND);
//            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//            response.getHeaders().set("Access-Control-Allow-Origin","*");
//            response.getHeaders().set("Cache-Control","no-cache");
//            String body= JSONUtil.toJsonStr(CommonResult.failed("路径有误"));
//            DataBuffer buffer =  response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
//            return response.writeWith(Mono.just(buffer));
//        }
//    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        //统一添加前缀，并且校验请求是否前缀开头
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String requestPath = request.getURI().getPath();
        if (requestPath.startsWith(apiPrefix)) {
            String substring = requestPath.substring(requestPath.indexOf(apiPrefix)+apiPrefix.length());
            ServerHttpRequest httpRequest = request.mutate().path(substring).contextPath(StringUtils.EMPTY).build();
            ServerWebExchange newExChange = exchange.mutate().request(httpRequest).build();
            return chain.filter(newExChange);
        } else {
            log.error("路径有误:{}",requestPath);
            response.setStatusCode(HttpStatus.NOT_FOUND);
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getHeaders().set("Access-Control-Allow-Origin","*");
            response.getHeaders().set("Cache-Control","no-cache");
            String body= JSONUtil.toJsonStr(CommonResult.failed("路径有误"));
            DataBuffer buffer =  response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
            return response.writeWith(Mono.just(buffer));
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

