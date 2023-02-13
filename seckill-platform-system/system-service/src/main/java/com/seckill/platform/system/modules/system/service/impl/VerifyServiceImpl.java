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
package com.seckill.platform.system.modules.system.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.seckill.framework.redisson.util.RedissonUtils;
import com.seckill.platform.system.common.exception.BadRequestException;
import com.seckill.platform.system.modules.system.service.VerifyService;
import com.seckill.platform.system.tools.domain.vo.EmailVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author Zheng Jie
 * @date 2018-12-26
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VerifyServiceImpl implements VerifyService {

    @Value("${code.expiration}")
    private Long expiration;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmailVo sendEmail(String email, String key) {
        EmailVo emailVo;
        String content;
        String redisKey = key + email;
        // 如果不存在有效的验证码，就创建一个新的
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("email/email.ftl");
        RBucket<Object> rBucket = RedissonUtils.getRBucket(redisKey);
        Object oldCode =  rBucket.get();
        if(oldCode == null){
            String code = RandomUtil.randomNumbers (6);
            // 存入缓存
            try{
                rBucket.set(code,expiration, TimeUnit.SECONDS);
            }catch (Exception ex){
                log.error("服务异常",ex);
                throw new BadRequestException("服务异常，请联系网站负责人");
            }
            content = template.render(Dict.create().set("code",code));
            emailVo = new EmailVo(Collections.singletonList(email),"ELADMIN后台管理系统",content);
        // 存在就再次发送原来的验证码
        } else {
            content = template.render(Dict.create().set("code",oldCode));
            emailVo = new EmailVo(Collections.singletonList(email),"ELADMIN后台管理系统",content);
        }
        return emailVo;
    }

    @Override
    public void validated(String key, String code) {
        RBucket<Object> rBucket = RedissonUtils.getRBucket(key);
        Object value = rBucket.get();
        if(value == null || !value.toString().equals(code)){
            throw new BadRequestException("无效验证码");
        } else {
            rBucket.delete();
        }
    }
}
