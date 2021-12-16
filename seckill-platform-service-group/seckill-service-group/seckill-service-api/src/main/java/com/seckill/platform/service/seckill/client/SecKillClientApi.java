package com.seckill.platform.service.seckill.client;

import com.seckill.framework.common.vo.Result;
import com.seckill.platform.service.seckill.dto.SecKillDto;
import com.seckill.platform.service.seckill.dto.SecKillInitDto;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author lenovo
 */
@FeignClient(name = "secKill-client")
public interface SecKillClientApi {

    /**
     * 执行秒杀
     * @param secKillDto
     * @return
     */
    public Result executeSecKill(SecKillDto secKillDto);

    /**
     * 初始化秒杀活动数据
     * @param secKillInitDto
     * @return
     */
    public Result<Boolean> initSecKillData(SecKillInitDto secKillInitDto);
}
