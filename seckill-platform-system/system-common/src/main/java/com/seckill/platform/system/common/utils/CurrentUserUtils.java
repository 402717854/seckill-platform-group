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
package com.seckill.platform.system.common.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.seckill.platform.system.common.dto.OnlineUserDto;
import com.seckill.platform.system.common.dto.UserLoginDto;
import com.seckill.platform.system.common.service.UserDetailService;
import com.seckill.platform.system.common.utils.enums.DataScopeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 获取当前登录的用户
 * @author Wang Yongsheng
 * @date 2019-01-17
 */
@Slf4j
public class CurrentUserUtils {

    private static final ThreadLocal<OnlineUserDto> LOCAL = new ThreadLocal<>();

    public static void put(OnlineUserDto sysUser) {
        LOCAL.set(sysUser);
    }

    public static OnlineUserDto get() {
        return LOCAL.get();
    }

    public static void remove() {
        LOCAL.remove();
    }

    /**
     * 获取当前登录的用户
     * @return UserDetails
     */
    public static UserLoginDto getCurrentUser() {
        UserDetailService userService = SpringContextHolder.getBean(UserDetailService.class);
        return userService.loadUserByUsername(getCurrentUsername());
    }

    /**
     * 获取系统用户名称
     *
     * @return 系统用户名称
     */
    public static String getCurrentUsername() {
        OnlineUserDto onlineUserDto = CurrentUserUtils.get();
        return onlineUserDto.getUserName();
    }

    /**
     * 获取系统用户ID
     * @return 系统用户ID
     */
    public static Long getCurrentUserId() {
        UserLoginDto userDetails = getCurrentUser();
        return new JSONObject(new JSONObject(userDetails).get("user")).get("id", Long.class);
    }

    /**
     * 获取当前用户的数据权限
     * @return /
     */
    public static List<Long> getCurrentUserDataScope(){
        UserLoginDto userDetails  = getCurrentUser();
        JSONArray array = JSONUtil.parseArray(new JSONObject(userDetails).get("dataScopes"));
        return JSONUtil.toList(array,Long.class);
    }

    /**
     * 获取数据权限级别
     * @return 级别
     */
    public static String getDataScopeType() {
        List<Long> dataScopes = getCurrentUserDataScope();
        if(dataScopes.size() != 0){
            return "";
        }
        return DataScopeEnum.ALL.getValue();
    }
}
