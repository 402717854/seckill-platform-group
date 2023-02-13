package com.seckill.platform.system.config.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.seckill.platform.common.api.ResultCode;
import com.seckill.platform.common.constant.AuthConstant;
import com.seckill.platform.system.common.dto.OnlineUserDto;
import com.seckill.platform.system.common.exception.BadRequestException;
import com.seckill.platform.system.common.utils.StringUtils;
import com.seckill.platform.system.common.utils.CurrentUserUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userHeader = request.getHeader(AuthConstant.USER_TOKEN_HEADER);
        if(StringUtils.isNotBlank(userHeader)){
            OnlineUserDto onlineUserDto = JSONObject.parseObject(userHeader, OnlineUserDto.class);
            CurrentUserUtils.put(onlineUserDto);
            return super.preHandle(request, response, handler);
        }
        throw new BadRequestException(ResultCode.TOKEN_FAILED);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CurrentUserUtils.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
