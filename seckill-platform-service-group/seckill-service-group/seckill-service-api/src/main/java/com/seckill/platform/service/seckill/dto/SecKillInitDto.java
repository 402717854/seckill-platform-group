package com.seckill.platform.service.seckill.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 秒杀初始化实体类
 * @author lenovo
 */
@Setter
@Getter
public class SecKillInitDto {

    /**
     * 参与秒杀商品ID
     */
    private String goodsId;
    /**
     * 参与秒杀商品库存
     */
    private long goodStack;
    /**
     * 秒杀活动ID
     */
    private String activityId;

    /**
     * 秒杀队列初始化容量
     */
    private int queueCapacity;

    /**
     * 最小线程数(核心线程数)
     */
    private int corePoolSize=4;
    /**
     * 最大线程数
     */
    private int maxPoolSize=10;
    /**
     * 线程池名称
     */
    private String threadPoolName;

    /**
     * 活动开始时间
     * yyyy-MM-dd HH:mm:ss
     */
    private String startTime="2023-01-01 00:00:00";
    /**
     * 活动结束时间
     * yyyy-MM-dd HH:mm:ss
     */
    private String endTime="2023-12-31 23:59:59";
}
