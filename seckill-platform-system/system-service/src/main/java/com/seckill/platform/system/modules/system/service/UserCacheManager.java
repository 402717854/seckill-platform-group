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
package com.seckill.platform.system.modules.system.service;

import cn.hutool.core.util.RandomUtil;
import com.seckill.framework.redisson.util.RedissonUtils;
import com.seckill.platform.system.common.utils.StringUtils;
import com.seckill.platform.system.config.bean.LoginProperties;
import com.seckill.platform.system.modules.system.service.dto.UserLoginDto;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author Zheng Jie
 * @description 用户缓存管理
 * @date 2022-05-26
 **/
@Component
public class UserCacheManager {

    @Value("${login.user-cache.idle-time}")
    private long idleTime;

    /**
     * 返回用户缓存
     * @param userName 用户名
     * @return JwtUserDto
     */
    public UserLoginDto getUserCache(String userName) {
        if (StringUtils.isNotEmpty(userName)) {
            // 获取数据
            Object obj = RedissonUtils.getMap(LoginProperties.cacheKey).get(userName);
            if(obj != null){
                return (UserLoginDto)obj;
            }
        }
        return null;
    }

    /**
     *  添加缓存到Redis
     * @param userName 用户名
     */
    @Async
    public void addUserCache(String userName, UserLoginDto user) {
        if (StringUtils.isNotEmpty(userName)) {
            // 添加数据, 避免数据同时过期
            long time = idleTime + RandomUtil.randomInt(900, 1800);
            RMap<Object, Object> rMap = RedissonUtils.getMap(LoginProperties.cacheKey);
            rMap.put(userName,user);
            rMap.expireIfSet(Duration.ofMillis(time));
        }
    }

    /**
     * 清理用户缓存信息
     * 用户信息变更时
     * @param userName 用户名
     */
    @Async
    public void cleanUserCache(String userName) {
        if (StringUtils.isNotEmpty(userName)) {
            // 清除数据
            RMap<Object, Object> rMap = RedissonUtils.getMap(LoginProperties.cacheKey);
            rMap.remove(userName);
        }
    }
}