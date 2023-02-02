package com.seckill.platform.admin.api;

import com.seckill.platform.admin.service.UmsAdminService;
import com.seckill.platform.common.api.CommonResult;
import com.seckill.platform.common.domain.UserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "UmsAdminApi", description = "后台用户管理服务提供API")
@RequestMapping("/api/admin")
public class UmsAdminApi {

    @Autowired
    private UmsAdminService adminService;

    @ApiOperation("根据用户名获取通用用户信息")
    @RequestMapping(value = "/loadByUsername", method = RequestMethod.GET)
    public CommonResult<UserDto> loadUserByUsername(@RequestParam String username) {
        UserDto userDTO = adminService.loadUserByUsername(username);
        return CommonResult.success(userDTO);
    }
}
