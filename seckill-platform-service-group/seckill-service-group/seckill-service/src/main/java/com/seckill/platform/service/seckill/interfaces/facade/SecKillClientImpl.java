package com.seckill.platform.service.seckill.interfaces.facade;

import com.seckill.framework.common.vo.Result;
import com.seckill.platform.service.seckill.application.service.SecKillApplicationService;
import com.seckill.platform.service.seckill.client.SecKillClientApi;
import com.seckill.platform.service.seckill.dto.SecKillDto;
import com.seckill.platform.service.seckill.dto.SecKillInitDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外接口实现
 * @author lenovo
 */
@RestController
@Slf4j
@RequestMapping("/seckill")
public class SecKillClientImpl implements SecKillClientApi {

    @Autowired
    private SecKillApplicationService secKillApplicationService;

    @Override
    @PostMapping("/execute")
    public Result executeSecKill(@RequestBody SecKillDto secKillDto) {
//        secKillApplicationService.executeSecKill(secKillDto);
        secKillApplicationService.asyncExecuteSecKill(secKillDto);
        return Result.success();
    }

    @Override
    @PostMapping("/init")
    public Result<Boolean> initSecKillData(@RequestBody SecKillInitDto secKillInitDto) {
        secKillApplicationService.initSecKillData(secKillInitDto);
        return Result.success();
    }
}
