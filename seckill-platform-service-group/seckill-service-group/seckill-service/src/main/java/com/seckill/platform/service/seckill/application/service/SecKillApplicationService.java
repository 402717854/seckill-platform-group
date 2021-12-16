package com.seckill.platform.service.seckill.application.service;

import com.seckill.platform.service.seckill.domain.seckill.service.SecKillService;
import com.seckill.platform.service.seckill.dto.SecKillDto;
import com.seckill.platform.service.seckill.dto.SecKillInitDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lenovo
 */
@Component
public class SecKillApplicationService {

    @Autowired
    private SecKillService secKillService;

    public void executeSecKill(SecKillDto secKillDto){
        secKillService.executeSecKill(secKillDto);
    }

    public void asyncExecuteSecKill(SecKillDto secKillDto){
        secKillService.asyncExecuteSecKill(secKillDto);
    }

    public void initSecKillData(SecKillInitDto secKillInitDto) {
        secKillService.initSecKillData(secKillInitDto);
    }
}
