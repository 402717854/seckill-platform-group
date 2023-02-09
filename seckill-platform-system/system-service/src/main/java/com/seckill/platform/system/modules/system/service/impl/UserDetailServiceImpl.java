package com.seckill.platform.system.modules.system.service.impl;

import com.seckill.platform.system.common.dto.UserLoginDto;
import com.seckill.platform.system.common.exception.BadRequestException;
import com.seckill.platform.system.common.exception.EntityNotFoundException;
import com.seckill.platform.system.common.service.UserDetailService;
import com.seckill.platform.system.modules.system.service.DataService;
import com.seckill.platform.system.modules.system.service.RoleService;
import com.seckill.platform.system.modules.system.service.UserCacheManager;
import com.seckill.platform.system.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
            try {
                user = userService.getLoginData(username);
            } catch (EntityNotFoundException e) {
                // SpringSecurity会自动转换UsernameNotFoundException为BadCredentialsException
                throw new UsernameNotFoundException(username, e);
            }
            if (user == null) {
                throw new UsernameNotFoundException("");
            } else {
                if (!user.getEnabled()) {
                    throw new BadRequestException("账号未激活！");
                }
                user.setDataScopes(dataService.getDeptIds(user));
                user.setAuthorityDtoList(roleService.mapToGrantedAuthorities(user));
                // 添加缓存数据
                userCacheManager.addUserCache(username, user);
            }
        }
        return user;
    }
}
