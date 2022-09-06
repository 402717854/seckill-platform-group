package com.seckill.platform.auth.service.impl;

import com.seckill.platform.auth.constant.MessageConstant;
import com.seckill.platform.auth.domain.SecurityUser;
//import com.seckill.platform.auth.service.UmsAdminService;
//import com.seckill.platform.auth.service.UmsMemberService;
import com.seckill.platform.auth.service.UmsAdminService;
import com.seckill.platform.auth.service.UmsMemberService;
import com.seckill.platform.common.constant.AuthConstant;
import com.seckill.platform.common.domain.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户管理业务类
 * Created by macro on 2020/6/19.
 */
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String clientId = request.getParameter("client_id");
        UserDto userDto;
        if(AuthConstant.ADMIN_CLIENT_ID.equals(clientId)){
//            userDto=new UserDto();
//            userDto.setUsername("admin");
//            userDto.setStatus(1);
//            userDto.setPassword(new BCryptPasswordEncoder().encode("123456"));
            userDto = adminService.loadUserByUsername(username);
        }else{
//            userDto=new UserDto();
//            userDto.setUsername("potal");
//            userDto.setStatus(1);
//            userDto.setPassword(new BCryptPasswordEncoder().encode("123456"));
            userDto = memberService.loadUserByUsername(username);
        }
        if (userDto==null) {
            throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        userDto.setClientId(clientId);
        SecurityUser securityUser = new SecurityUser(userDto);
        if (!securityUser.isEnabled()) {
            throw new DisabledException(MessageConstant.ACCOUNT_DISABLED);
        } else if (!securityUser.isAccountNonLocked()) {
            throw new LockedException(MessageConstant.ACCOUNT_LOCKED);
        } else if (!securityUser.isAccountNonExpired()) {
            throw new AccountExpiredException(MessageConstant.ACCOUNT_EXPIRED);
        } else if (!securityUser.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(MessageConstant.CREDENTIALS_EXPIRED);
        }
        return securityUser;
    }

}
