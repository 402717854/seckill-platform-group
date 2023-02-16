/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.seckill.platform.system.modules.system.rest;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.seckill.framework.redisson.util.RedissonUtils;
import com.seckill.platform.common.api.CommonResult;
import com.seckill.platform.common.api.ResultCode;
import com.seckill.platform.common.constant.AuthConstant;
import com.seckill.platform.system.common.annotation.rest.AnonymousDeleteMapping;
import com.seckill.platform.system.common.annotation.rest.AnonymousGetMapping;
import com.seckill.platform.system.common.annotation.rest.AnonymousPostMapping;
import com.seckill.platform.system.common.config.RsaProperties;
import com.seckill.platform.system.common.exception.BadRequestException;
import com.seckill.platform.system.common.utils.RsaUtils;
import com.seckill.platform.system.common.utils.StringUtils;
import com.seckill.platform.system.config.bean.LoginCodeEnum;
import com.seckill.platform.system.config.bean.LoginProperties;
import com.seckill.platform.system.config.bean.SecurityProperties;
import com.seckill.platform.system.logging.annotation.Log;
import com.seckill.platform.system.modules.system.service.OnlineUserService;
import com.seckill.platform.system.modules.system.service.UserDetailService;
import com.seckill.platform.system.modules.system.service.dto.AuthUserDto;
import com.seckill.platform.system.modules.system.service.dto.UserLoginDto;
import com.seckill.platform.system.modules.system.service.openfegin.AuthService;
import com.seckill.platform.system.urils.CurrentUserCacheUtils;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Api(tags = "系统：系统授权接口")
public class AuthorizationController {
    private final SecurityProperties properties;
    private final OnlineUserService onlineUserService;
    private final AuthService authService;
    private final UserDetailService userDetailService;
    @Resource
    private LoginProperties loginProperties;

    @Log("用户登录")
    @ApiOperation("登录授权")
    @AnonymousPostMapping(value = "/login")
    public CommonResult<Object> login(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
        // 密码解密
        String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());
        // 查询验证码
        String code = (String) RedissonUtils.getRBucket(authUser.getUuid()).get();
        // 清除验证码
        RedissonUtils.getRBucket(authUser.getUuid()).delete();
        if (StringUtils.isBlank(code)) {
            throw new BadRequestException("验证码不存在或已过期");
        }
        if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
            throw new BadRequestException("验证码错误");
        }
        Map<String, String> params = new HashMap<>();
        params.put("client_id", AuthConstant.ADMIN_CLIENT_ID);
        params.put("client_secret","123456");
        params.put("grant_type","password");
        params.put("username",authUser.getUsername());
        params.put("password",password);
        CommonResult commonResult = authService.getAccessToken(params);
        if(ResultCode.SUCCESS.getCode()!=commonResult.getCode()){
            throw new BadRequestException("登录账号认证失败");
        }
        LinkedHashMap data = (LinkedHashMap) commonResult.getData();
        String token = (String) data.get("token");
        UserLoginDto userLoginDto = userDetailService.loadUserByUsername(authUser.getUsername());
        // 保存在线信息
        onlineUserService.save(userLoginDto, token, request);
        if (loginProperties.isSingleLogin()) {
            //踢掉之前已经登录的token
            onlineUserService.checkLoginOnUser(authUser.getUsername(), token);
        }
        // 返回 token 与 用户信息
        Map<String, Object> authInfo = new HashMap<String, Object>(2) {{
            put("token", properties.getTokenStartWith() + token);
            put("user", userLoginDto);
        }};
        return CommonResult.success(authInfo);
    }

    @ApiOperation("获取用户信息")
    @GetMapping(value = "/info")
    public CommonResult<Object> getUserInfo() {
        return CommonResult.success(CurrentUserCacheUtils.getCurrentUser());
    }

    @ApiOperation("获取验证码")
    @AnonymousGetMapping(value = "/code")
    public CommonResult<Object> getCode() {
        // 获取运算的结果
        Captcha captcha = loginProperties.getCaptcha();
        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == LoginCodeEnum.ARITHMETIC.ordinal() && captchaValue.contains(".")) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        // 保存
        RedissonUtils.getRBucket(uuid).set(captchaValue, loginProperties.getLoginCode().getExpiration(), TimeUnit.MINUTES);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return CommonResult.success(imgResult);
    }

    @ApiOperation("退出登录")
    @AnonymousDeleteMapping(value = "/logout")
    public CommonResult<Object> logout(HttpServletRequest request) {
        final String token = request.getHeader(properties.getHeader());
        if (token != null && token.startsWith(properties.getTokenStartWith())) {
            String realToken = token.replace(AuthConstant.TOKEN_PREFIX, "");
            userDetailService.logout(realToken);
            onlineUserService.logout(realToken);
            return CommonResult.success("退出登录成功");
        }
        return CommonResult.failed("退出登录失败");
    }
}
