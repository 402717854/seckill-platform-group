package com.seckill.platform.auth.service;

import com.seckill.platform.common.api.CommonResult;
import com.seckill.platform.common.domain.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by macro on 2019/10/18.
 */
@FeignClient("seckill-platform-admin")
public interface UmsAdminService {

    @GetMapping("/api/admin/loadByUsername")
    CommonResult<UserDto> loadUserByUsername(@RequestParam String username);
}
