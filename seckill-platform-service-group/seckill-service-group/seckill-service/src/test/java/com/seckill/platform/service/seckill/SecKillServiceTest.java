package com.seckill.platform.service.seckill;

import com.seckill.platform.service.seckill.application.service.SecKillApplicationService;
import com.seckill.platform.service.seckill.dto.SecKillDto;
import com.seckill.platform.service.seckill.dto.SecKillInitDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SecKillServiceTest {

    @Autowired
    private SecKillApplicationService secKillApplicationService;

    @Test
    public void testExecuteSecKill(){
//        SecKillDto secKillDto = new SecKillDto();
//        secKillDto.setActivityId("activity123456");
//        secKillDto.setUserId("user123");
//        secKillApplicationService.executeSecKill(secKillDto);
        for (int i = 0; i < 1; i++) {
            int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SecKillDto secKillDto = new SecKillDto();
                    secKillDto.setActivityId("activity123456");
                    secKillDto.setUserId("user123"+ finalI);
                    secKillApplicationService.executeSecKill(secKillDto);
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
    @Test
    public void testAsyncExecuteSecKill(){
        SecKillDto secKillDto = new SecKillDto();
        secKillApplicationService.asyncExecuteSecKill(secKillDto);
    }
    @Test
    public void testInitSecKillData() {
        SecKillInitDto secKillInitDto = new SecKillInitDto();
        secKillInitDto.setActivityId("activity123456");
        secKillInitDto.setGoodStack(10);
        secKillApplicationService.initSecKillData(secKillInitDto);
    }
}
