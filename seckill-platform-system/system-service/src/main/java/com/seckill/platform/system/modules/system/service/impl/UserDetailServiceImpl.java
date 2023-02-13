package com.seckill.platform.system.modules.system.service.impl;

import com.seckill.platform.system.common.exception.BadRequestException;
import com.seckill.platform.system.modules.system.service.*;
import com.seckill.platform.system.modules.system.service.dto.UserLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserCacheManager userCacheManager;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DataService dataService;
    @Override
    public UserLoginDto loadUserByUsername(String username) {
        UserLoginDto user = userCacheManager.getUserCache(username);
        if(user == null){
            user = userService.getLoginData(username);
            if (!user.getEnabled()) {
                throw new BadRequestException("账号未激活！");
            }
            user.setDataScopes(dataService.getDeptIds(user));
            user.setAuthorityDtoList(roleService.mapToGrantedAuthorities(user));
            // 添加缓存数据
            userCacheManager.addUserCache(username, user);
        }
        return user;
    }
}
