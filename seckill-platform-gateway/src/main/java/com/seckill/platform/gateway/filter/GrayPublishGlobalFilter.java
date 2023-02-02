package com.seckill.platform.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.seckill.platform.gateway.context.GrayRequestContextHolder;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.seckill.platform.common.constant.GrayConstant;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * 灰度发布
 */
@Component
public class GrayPublishGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //① 解析请求头，查看是否存在灰度发布的请求头信息，如果存在则将其放置在ThreadLocal中
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if (headers.containsKey(GrayConstant.GRAY_HEADER)){
            String gray = headers.getFirst(GrayConstant.GRAY_HEADER);
            if (StrUtil.equals(gray,GrayConstant.GRAY_VALUE)){
                //②设置灰度标记
                GrayRequestContextHolder.setGrayTag(true);
            }
        }
        //③ 将灰度标记放入请求头中
        ServerHttpRequest tokenRequest = exchange.getRequest().mutate()
                //将灰度标记传递过去
                .header(GrayConstant.GRAY_HEADER,GrayRequestContextHolder.getGrayTag().toString())
                .build();
        ServerWebExchange build = exchange.mutate().request(tokenRequest).build();
        return chain.filter(build);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
