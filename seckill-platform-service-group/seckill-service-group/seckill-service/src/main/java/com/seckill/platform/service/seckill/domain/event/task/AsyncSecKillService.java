package com.seckill.platform.service.seckill.domain.event.task;

import com.seckill.framework.redisson.util.RedissonUtils;
import com.seckill.platform.service.seckill.dto.SecKillDto;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 异步秒杀服务
 * @author lenovo
 */
@Service
@Slf4j
public class AsyncSecKillService {

    /**
     *  异步执行
     * @param secKillDto
     * @param secKillStockAtomicLong
     * @param secKillSuccessRMap
     */
    @Async(value = "secKill-thread-pool")
    public void asyncSecKill(SecKillDto secKillDto, RAtomicLong secKillStockAtomicLong, RMap<Object, Object> secKillSuccessRMap, RSet<Object> secKillingSet){
        //5判断是否有库存
        long stock2 = secKillStockAtomicLong.get();
        if(stock2<=0){
            //清空抢购进行中set
            secKillingSet.clear();
            log.error(secKillDto.getUserId()+"异步抢购商品，已无库存");
            return;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //6执行库存扣减
        RScript rScript = RedissonUtils.getScript();
        String luaScript = "local num=redis.call('get', KEYS[1]) if tonumber(num)>0 then return redis" +
                ".call('decr', KEYS[1]) else return -1 end";
        Long stock3 = rScript.eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.INTEGER, Arrays.asList("secKill-stock-" + secKillDto.getActivityId()), Collections.emptyList());
        if(stock3<0){
            //清空抢购进行中set
            secKillingSet.clear();
            log.error(secKillDto.getUserId()+"参加该活动，商品已被抢购一空");
            return;
        }
        //7用户抢购成功
        //生成订单ID
        String orderId="orderId";
        Map<String,Object> map=new HashMap<>();
        map.put(orderId,secKillDto.getActivityId());
        //放入抢购成功Map中
        secKillSuccessRMap.fastPut(secKillDto.getUserId(),map);
        //将抢购成功的用户移除抢购进行中set
        secKillingSet.remove(secKillDto.getUserId());
        //8订单消息发送订单服务
        log.info(secKillDto.getUserId()+"秒杀成功，并发送mq消息到订单服务生成订单");
    }
}
