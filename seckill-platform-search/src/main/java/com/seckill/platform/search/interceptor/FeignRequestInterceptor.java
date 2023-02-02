package com.seckill.platform.search.interceptor;

import cn.hutool.core.util.StrUtil;
import com.seckill.platform.common.constant.GrayConstant;
import com.seckill.platform.search.context.GrayRequestContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
@Component
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        Map<String, String> headers = getHeaders(httpServletRequest);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            //② 设置请求头到新的Request中
            template.header(entry.getKey(), entry.getValue());
        }
    }
    /**
     * 获取原请求头
     */
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                String value = request.getHeader(key);
                //将灰度标记的请求头透传给下个服务
                if (StrUtil.equals(GrayConstant.GRAY_HEADER,key)&&Boolean.TRUE.toString().equals(value)){
                    //① 保存灰度发布的标记
                    GrayRequestContextHolder.setGrayTag(true);
                    map.put(key, value);
                }
            }
        }
        return map;
    }
}
