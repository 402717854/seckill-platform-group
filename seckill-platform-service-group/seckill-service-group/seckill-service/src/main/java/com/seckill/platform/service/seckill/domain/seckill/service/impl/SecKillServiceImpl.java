package com.seckill.platform.service.seckill.domain.seckill.service.impl;

import com.seckill.framework.redisson.util.RedissonUtils;
import com.seckill.framework.threadpool.support.PlatformThreadPoolTaskExecutor;
import com.seckill.platform.service.seckill.domain.event.task.AsyncSecKillService;
import com.seckill.platform.service.seckill.domain.seckill.service.SecKillService;
import com.seckill.platform.service.seckill.dto.SecKillDto;
import com.seckill.platform.service.seckill.dto.SecKillInitDto;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 秒杀接口实现类
 * @author wys
 */
@Service
@Slf4j
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    private AsyncSecKillService asyncSecKillService;

    @Autowired
    private DefaultListableBeanFactory defaultListableBeanFactory;

    @Override
    public void executeSecKill(SecKillDto secKillDto) {
        log.info("用户{},正在执行秒杀活动",secKillDto.getUserId());
        //1判断是否在抢购期内
        //2判断用户是否已抢购成功
        RMap<Object, Object> secKillSuccessRMap = RedissonUtils.getMap("secKill-success-"+secKillDto.getActivityId());
        boolean containsSecKillSuccessUser = secKillSuccessRMap.containsKey(secKillDto.getUserId());
        if(containsSecKillSuccessUser){
            log.error("该用户{}已经抢购成功,只能抢购一次",secKillDto.getUserId());
            return;
        }
        //3判断是用户否正在抢购
        RBoundedBlockingQueue<Object> secKillUserQueue = RedissonUtils.getRBoundedBlockingQueue("secKill-user-" + secKillDto.getActivityId());
        boolean secKillUserQueueContains = secKillUserQueue.contains(secKillDto.getUserId());
        if(secKillUserQueueContains){
            log.error("该用户{}正在抢购中",secKillDto.getUserId());
            return;
        }
        //4判断是否有库存
        RAtomicLong secKillStockAtomicLong = RedissonUtils.getAtomicLong("secKill-stock-" + secKillDto.getActivityId());
        long stock = secKillStockAtomicLong.get();
        if(stock<=0){
            log.error("该活动商品已无库存");
            secKillUserQueue.clear();
            return;
        }
        //5用户进入抢购队列
        try{
            secKillUserQueue.add(secKillDto.getUserId());
        }catch (Exception ex){
            log.error("现在抢购人数太多");
            return;
        }
        //6判断是否有库存
        long stock2 = secKillStockAtomicLong.get();
        if(stock2<=0){
            log.error("该活动商品已无库存");
            secKillUserQueue.clear();
            return;
        }
        //7执行库存扣减
        RScript rScript = RedissonUtils.getScript();
        String luaScript = "local num=redis.call('get', KEYS[1]) if tonumber(num)>0 then return redis" +
                ".call('decr', KEYS[1]) else return -1 end";
        Long stock3 = rScript.eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.INTEGER, Arrays.asList("secKill-stock-" + secKillDto.getActivityId()), Collections.emptyList());
        if(stock3<0){
            log.error("该活动商品已被抢购一空");
            secKillUserQueue.clear();
            return;
        }
        //将抢购队列中的用户移除
        secKillUserQueue.poll();
        //8用户抢购成功
        //生成订单ID
        String orderId="orderId";
        Map<String,Object> map=new HashMap<>();
        map.put(orderId,secKillDto.getActivityId());
        secKillSuccessRMap.fastPut(secKillDto.getUserId(),map);
        log.info("用户{}抢购成功",secKillDto.getUserId());
    }
    @Override
    public void asyncExecuteSecKill(SecKillDto secKillDto) {
        log.info("用户{},正在执行秒杀活动",secKillDto.getUserId());
        //1判断是否在抢购期内
        //2判断用户是否正在抢购(防止重复提交)
        RSet<Object> secKillingSet = RedissonUtils.getSet("secKilling-user-" + secKillDto.getActivityId());
        boolean addUserId = secKillingSet.add(secKillDto.getUserId());
        if(!addUserId){
            log.error("该用户{}正在抢购中",secKillDto.getUserId());
            return;
        }
        //3判断用户是否已抢购成功
        RMap<Object, Object> secKillSuccessRMap = RedissonUtils.getMap("secKill-success-"+secKillDto.getActivityId());
        boolean containsSecKillSuccessUser = secKillSuccessRMap.containsKey(secKillDto.getUserId());
        if(containsSecKillSuccessUser){
            log.error("该用户{}已经抢购成功",secKillDto.getUserId());
            return;
        }
        //4判断是否有库存
        RAtomicLong secKillStockAtomicLong = RedissonUtils.getAtomicLong("secKill-stock-" + secKillDto.getActivityId());
        long stock = secKillStockAtomicLong.get();
        if(stock<=0){
            //清空抢购进行中set
            secKillingSet.clear();
            log.info(secKillDto.getUserId()+"参加该活动，商品已无库存");
            return;
        }
        //5 异步执行秒杀
        try {
            asyncSecKillService.asyncSecKill(secKillDto,secKillStockAtomicLong,secKillSuccessRMap,secKillingSet);
        }catch (Exception e){
            log.error(e.getMessage());
            log.error(e.getCause().getMessage());
        }
    }

    @Override
    public void initSecKillData(SecKillInitDto secKillInitDto) {
        //初始化库存
        RAtomicLong secKillStockAtomicLong = RedissonUtils.getAtomicLong("secKill-stock-" + secKillInitDto.getActivityId());
        secKillStockAtomicLong.set(secKillInitDto.getGoodStack());
        //初始化分布式队列
        RBoundedBlockingQueue<Object> secKillUserQueue = RedissonUtils.getRBoundedBlockingQueue("secKill-user-" + secKillInitDto.getActivityId());
        Long capacity=secKillInitDto.getGoodStack()+(secKillInitDto.getGoodStack()/2);
        secKillUserQueue.trySetCapacity(capacity.intValue());
        //刷新本地线程池
        if(StringUtils.hasText(secKillInitDto.getThreadPoolName())){
            PlatformThreadPoolTaskExecutor platformThreadPoolTaskExecutor = getExecutor(secKillInitDto.getThreadPoolName());
            if(platformThreadPoolTaskExecutor!=null){
                platformThreadPoolTaskExecutor.setCorePoolSize(secKillInitDto.getCorePoolSize());
                platformThreadPoolTaskExecutor.setMaxPoolSize(secKillInitDto.getMaxPoolSize());
                platformThreadPoolTaskExecutor.setQueueCapacity(secKillUserQueue.remainingCapacity());
                platformThreadPoolTaskExecutor.initialize();
            }
        }
    }

    private PlatformThreadPoolTaskExecutor getExecutor(String threadPoolName) {
        boolean containsBean = defaultListableBeanFactory.containsBean(threadPoolName);
        if(!containsBean){
            return null;
        }
        PlatformThreadPoolTaskExecutor myThreadPoolTaskExecutor = defaultListableBeanFactory.getBean(threadPoolName, PlatformThreadPoolTaskExecutor.class);
        return myThreadPoolTaskExecutor;
    }
}
