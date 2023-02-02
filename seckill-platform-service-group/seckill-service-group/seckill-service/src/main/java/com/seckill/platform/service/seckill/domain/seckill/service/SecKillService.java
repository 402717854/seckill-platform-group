package com.seckill.platform.service.seckill.domain.seckill.service;

import com.seckill.platform.service.seckill.dto.SecKillDto;
import com.seckill.platform.service.seckill.dto.SecKillInitDto;

/**
 * @author lenovo
 */
public interface SecKillService {

    /**
     * 执行秒杀
     * @param secKillDto
     * @return
     */
    public void executeSecKill(SecKillDto secKillDto);
    /**
     * 异步执行秒杀
     * @param secKillDto
     * @return
     */
    public void asyncExecuteSecKill(SecKillDto secKillDto);

    /**
     * 初始化秒杀活动数据
     * @param secKillInitDto
     */
    public void initSecKillData(SecKillInitDto secKillInitDto);
}
