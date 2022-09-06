package com.seckill.platform.admin.service;

import com.seckill.platform.admin.model.UmsAdmin;

/**
 * 后台用户缓存操作类
 * Created by macro on 2020/3/13.
 */
public interface UmsAdminCacheService {
    /**
     * 删除后台用户缓存
     */
    void delAdmin(Long adminId);

    /**
     * 获取缓存后台用户信息
     */
    UmsAdmin getAdmin(Long adminId);

    /**
     * 设置缓存后台用户信息
     */
    void setAdmin(UmsAdmin admin);

    /**
     * 设置缓存后台登录用户信息
     */
    void setLoginAdmin(String token,UmsAdmin admin);
    /**
     * 获取缓存后台登录用户信息
     */
    String getLoginAdmin(String token,UmsAdmin admin);
    /**
     * 删除后台登录用户信息
     */
    void delLoginAdmin(String token);
}
