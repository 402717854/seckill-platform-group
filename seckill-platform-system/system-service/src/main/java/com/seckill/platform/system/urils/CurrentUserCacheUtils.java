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
package com.seckill.platform.system.urils;

import com.seckill.platform.system.common.dto.OnlineUserDto;
import com.seckill.platform.system.common.utils.CurrentUserUtils;
import com.seckill.platform.system.common.utils.SpringContextHolder;
import com.seckill.platform.system.common.utils.enums.DataScopeEnum;
import com.seckill.platform.system.modules.system.service.UserDetailService;
import com.seckill.platform.system.modules.system.service.dto.UserLoginDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 获取当前登录的用户
 * @author Wang Yongsheng
 * @date 2019-01-17
 */
@Slf4j
public class CurrentUserCacheUtils {

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
        return userDetails.getId();
    }

    /**
     * 获取当前用户的数据权限
     * @return /
     */
    public static List<Long> getCurrentUserDataScope(){
        UserLoginDto userDetails  = getCurrentUser();
        List<Long> dataScopes = userDetails.getDataScopes();
        return dataScopes;
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
