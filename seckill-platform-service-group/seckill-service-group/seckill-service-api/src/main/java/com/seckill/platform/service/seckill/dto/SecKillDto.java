package com.seckill.platform.service.seckill.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 秒杀传值实体类
 * @author lenovo
 */
@Setter
@Getter
public class SecKillDto {

    /**
     * 参与秒杀用户
     */
    private String userId;
    /**
     * 秒杀活动ID
     */
    private String activityId;
}
