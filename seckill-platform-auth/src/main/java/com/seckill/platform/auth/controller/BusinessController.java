package com.seckill.platform.auth.controller;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.seckill.platform.common.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

/**
 * 认证中心业务功能
 * Created by macro on 2020/7/17.
 */
@RestController
@Api(tags = "BusinessController", description = "认证中心业务功能")
@RequestMapping("/bus")
public class BusinessController {

    @Autowired
    private JdbcClientDetailsService clientDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @ApiOperation("Oauth2添加客户端信息")
    @RequestMapping(value = "/addClientDetails", method = RequestMethod.POST)
    public CommonResult addClientDetails(@RequestBody Map<String,Object> paramMap){
        BaseClientDetails baseClientDetails = new BaseClientDetails();
        baseClientDetails.setClientId("admin-app");
        baseClientDetails.setClientSecret(passwordEncoder.encode("12345"));
        baseClientDetails.setScope(Arrays.asList("all"));
        baseClientDetails.setAuthorizedGrantTypes(Arrays.asList("client_credentials","password","authorization_code","implicit","refresh_token"));
        baseClientDetails.setAccessTokenValiditySeconds(31536000);
        baseClientDetails.setRefreshTokenValiditySeconds(2592000);
        baseClientDetails.setAutoApproveScopes(Arrays.asList("false"));
        BeanMap beanMap = BeanMap.create(baseClientDetails);
        beanMap.putAll(paramMap);
        clientDetailsService.addClientDetails(baseClientDetails);
        return CommonResult.success(null);
    }
}
