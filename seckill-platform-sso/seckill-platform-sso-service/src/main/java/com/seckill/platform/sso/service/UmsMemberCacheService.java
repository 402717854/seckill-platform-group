package com.seckill.platform.sso.service;

import com.seckill.platform.sso.model.UmsMember;

/**
 * 会员信息缓存业务类
 * Created by macro on 2020/3/14.
 */
public interface UmsMemberCacheService {
    /**
     * 删除会员用户缓存
     */
    void delMember(Long memberId);

    /**
     * 获取会员用户缓存
     */
    UmsMember getMember(Long memberId);

    /**
     * 设置会员用户缓存
     */
    void setMember(UmsMember member);

    /**
     * 设置验证码
     */
    void setAuthCode(String telephone, String authCode);

    /**
     * 获取验证码
     */
    String getAuthCode(String telephone);
    /**
     * 删除登录会员用户缓存
     */
    void delLoginMember(String token);

    /**
     * 获取登录会员用户缓存
     */
    String getLoginMember(String token);

    void setLoginMember(String token,UmsMember member);
}
