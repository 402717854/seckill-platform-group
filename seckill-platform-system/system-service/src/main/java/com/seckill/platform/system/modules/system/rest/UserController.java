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
package com.seckill.platform.system.modules.system.rest;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.seckill.platform.common.api.CommonResult;
import com.seckill.platform.system.common.config.RsaProperties;
import com.seckill.platform.system.common.exception.BadRequestException;
import com.seckill.platform.system.common.utils.CurrentUserUtils;
import com.seckill.platform.system.common.utils.PageUtil;
import com.seckill.platform.system.common.utils.RsaUtils;
import com.seckill.platform.system.common.utils.enums.CodeEnum;
import com.seckill.platform.system.logging.annotation.Log;
import com.seckill.platform.system.modules.system.domain.Dept;
import com.seckill.platform.system.modules.system.domain.User;
import com.seckill.platform.system.modules.system.domain.vo.UserPassVo;
import com.seckill.platform.system.modules.system.service.*;
import com.seckill.platform.system.modules.system.service.dto.RoleSmallDto;
import com.seckill.platform.system.modules.system.service.dto.UserDto;
import com.seckill.platform.system.modules.system.service.dto.UserLoginDto;
import com.seckill.platform.system.modules.system.service.dto.UserQueryCriteria;
import com.seckill.platform.system.urils.CurrentUserCacheUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Wang Yongsheng
 * @date 2018-11-23
 */
@Api(tags = "系统：用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final DataService dataService;
    private final DeptService deptService;
    private final RoleService roleService;
    private final VerifyService verificationCodeService;

    private final UserDetailService userDetailService;

    @ApiOperation("导出用户数据")
    @GetMapping(value = "/download")
    public void exportUser(HttpServletResponse response, UserQueryCriteria criteria) throws IOException {
        userService.download(userService.queryAll(criteria), response);
    }

    @ApiOperation("查询用户")
    @GetMapping
    public ResponseEntity<Object> queryUser(UserQueryCriteria criteria, Pageable pageable){
        if (!ObjectUtils.isEmpty(criteria.getDeptId())) {
            criteria.getDeptIds().add(criteria.getDeptId());
            // 先查找是否存在子节点
            List<Dept> data = deptService.findByPid(criteria.getDeptId());
            // 然后把子节点的ID都加入到集合中
            criteria.getDeptIds().addAll(deptService.getDeptChildren(data));
        }
        // 数据权限
        List<Long> dataScopes = dataService.getDeptIds(userService.findByName(CurrentUserCacheUtils.getCurrentUsername()));
        // criteria.getDeptIds() 不为空并且数据权限不为空则取交集
        if (!CollectionUtils.isEmpty(criteria.getDeptIds()) && !CollectionUtils.isEmpty(dataScopes)){
            // 取交集
            criteria.getDeptIds().retainAll(dataScopes);
            if(!CollectionUtil.isEmpty(criteria.getDeptIds())){
                return new ResponseEntity<>(userService.queryAll(criteria,pageable),HttpStatus.OK);
            }
        } else {
            // 否则取并集
            criteria.getDeptIds().addAll(dataScopes);
            return new ResponseEntity<>(userService.queryAll(criteria,pageable),HttpStatus.OK);
        }
        return new ResponseEntity<>(PageUtil.toPage(null,0),HttpStatus.OK);
    }

    @Log("新增用户")
    @ApiOperation("新增用户")
    @PostMapping
    public ResponseEntity<Object> createUser(@Validated @RequestBody User resources){
        checkLevel(resources);
        // 默认密码 123456
        try {
            resources.setPassword(BCrypt.hashpw("123456"));
            userService.create(resources);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Log("修改用户")
    @ApiOperation("修改用户")
    @PutMapping
    public ResponseEntity<Object> updateUser(@Validated(User.Update.class) @RequestBody User resources) throws Exception {
        checkLevel(resources);
        userService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("修改用户：个人中心")
    @ApiOperation("修改用户：个人中心")
    public ResponseEntity<Object> centerUser(@Validated(User.Update.class) @RequestBody User resources){
        if(!resources.getId().equals(CurrentUserCacheUtils.getCurrentUserId())){
            throw new BadRequestException("不能修改他人资料");
        }
        userService.updateCenter(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除用户")
    @ApiOperation("删除用户")
    @DeleteMapping
    public ResponseEntity<Object> deleteUser(@RequestBody Set<Long> ids){
        for (Long id : ids) {
            Integer currentLevel =  Collections.min(roleService.findByUsersId(CurrentUserCacheUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
            Integer optLevel =  Collections.min(roleService.findByUsersId(id).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
            if (currentLevel > optLevel) {
                throw new BadRequestException("角色权限不足，不能删除：" + userService.findById(id).getUsername());
            }
        }
        userService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("修改密码")
    @PostMapping(value = "/updatePass")
    public ResponseEntity<Object> updateUserPass(@RequestBody UserPassVo passVo) throws Exception {
        String oldPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey,passVo.getOldPass());
        String newPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey,passVo.getNewPass());
        UserDto user = userService.findByName(CurrentUserUtils.getCurrentUsername());
        String oldPassBC = BCrypt.hashpw(oldPass);
        String newPassBC = BCrypt.hashpw(newPass);
        if(!oldPassBC.equals(user.getPassword())){
            throw new BadRequestException("修改失败，旧密码错误");
        }
        if(newPassBC.equals(user.getPassword())){
            throw new BadRequestException("新密码不能与旧密码相同");
        }
        userService.updatePass(user.getUsername(),newPassBC);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("修改头像")
    @PostMapping(value = "/updateAvatar")
    public ResponseEntity<Object> updateUserAvatar(@RequestParam MultipartFile avatar){
        return new ResponseEntity<>(userService.updateAvatar(avatar), HttpStatus.OK);
    }

    @Log("修改邮箱")
    @ApiOperation("修改邮箱")
    public ResponseEntity<Object> updateUserEmail(@PathVariable String code,@RequestBody User user) throws Exception {
        String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey,user.getPassword());
        UserDto userDto = userService.findByName(CurrentUserUtils.getCurrentUsername());
        String passBC = BCrypt.hashpw(password);
        if(!passBC.equals(userDto.getPassword())){
            throw new BadRequestException("密码错误");
        }
        verificationCodeService.validated(CodeEnum.EMAIL_RESET_EMAIL_CODE.getKey() + user.getEmail(), code);
        userService.updateEmail(userDto.getUsername(),user.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 如果当前用户的角色级别低于创建用户的角色级别，则抛出权限不足的错误
     * @param resources /
     */
    private void checkLevel(User resources) {
        Integer currentLevel =  Collections.min(roleService.findByUsersId(CurrentUserCacheUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
        Integer optLevel = roleService.findByRoles(resources.getRoles());
        if (currentLevel > optLevel) {
            throw new BadRequestException("角色权限不足");
        }
    }

    @GetMapping("/loadUserByUsername")
    public CommonResult<UserLoginDto> loadUserByUsername(@Validated @NotEmpty(message = "用户名不能为空") String username){
        UserLoginDto userLoginDto = userDetailService.loadUserByUsername(username);
        return CommonResult.success(userLoginDto);
    }
}
