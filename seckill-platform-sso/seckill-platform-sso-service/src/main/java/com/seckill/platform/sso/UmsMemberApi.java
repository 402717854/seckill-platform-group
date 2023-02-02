package com.seckill.platform.sso;

import com.seckill.platform.common.api.CommonResult;
import com.seckill.platform.common.domain.UserDto;
import com.seckill.platform.sso.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "UmsMemberController", description = "会员服务提供API")
@RequestMapping("/api/sso")
public class UmsMemberApi {

    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("根据用户名获取通用用户信息")
    @RequestMapping(value = "/loadByUsername", method = RequestMethod.GET)
    public CommonResult<UserDto> loadUserByUsername(@RequestParam String username) {
        return CommonResult.success(memberService.loadUserByUsername(username));
    }
}
