package com.seckill.platform.admin.service.impl;

import cn.hutool.json.JSONUtil;
import com.seckill.platform.admin.model.UmsAdmin;
import com.seckill.platform.admin.service.UmsAdminCacheService;
import com.seckill.platform.admin.service.UmsAdminService;
import com.seckill.platform.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * UmsAdminCacheService实现类
 * Created by macro on 2020/3/13.
 */
@Service
public class UmsAdminCacheServiceImpl implements UmsAdminCacheService {
    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private RedisService redisService;
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE;
    @Value("${redis.key.admin}")
    private String REDIS_KEY_ADMIN;

    @Override
    public void delAdmin(Long adminId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + adminId;
        redisService.del(key);
    }

    @Override
    public UmsAdmin getAdmin(Long adminId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + adminId;
        return (UmsAdmin) redisService.get(key);
    }

    @Override
    public void setAdmin(UmsAdmin admin) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + admin.getId();
        redisService.set(key, admin, REDIS_EXPIRE);
    }

    @Override
    public void setLoginAdmin(String token, UmsAdmin admin) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + token;
        String string = JSONUtil.parse(admin).toString();
        redisService.set(key,string,REDIS_EXPIRE);
    }

    @Override
    public String getLoginAdmin(String token, UmsAdmin admin) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + token;
        return (String) redisService.get(key);
    }

    @Override
    public void delLoginAdmin(String token) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + token;
        redisService.del(key);
    }
}
