package com.seckill.platform.auth.service;

import com.seckill.platform.common.domain.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("seckill-platform-system")
public interface SysAdminService {

    @GetMapping("/api/users/loadUserByUsername")
    UserDto loadUserByUsername(@RequestParam String username);
}
