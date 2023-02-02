package com.seckill.platform.service.seckill.domain.seckill.service.impl;

import cn.hutool.core.date.DateUtil;
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

import java.util.*;

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
        //1.0判断用户是否非法
        //1.1判断是否在抢购期内
        RMap<Object, Object> activityMap = RedissonUtils.getMap("secKill-activity-" + secKillDto.getActivityId());
        if(activityMap==null){
            log.error("无此活动");
            return;
        }
        Long startTime = (Long)activityMap.get("startTime");
        Long endTime = (Long)activityMap.get("endTime");
        long nowTime = new Date().getTime();
        if(nowTime<startTime){
            log.error("活动还没开始");
            return;
        }
        if(nowTime>endTime){
            log.error("活动已经结束");
            return;
        }
        //1.2判断是否在抢购期内
        RAtomicLong secKillStockAtomicLong = RedissonUtils.getAtomicLong("secKill-stock-" + secKillDto.getActivityId());
        long stock = secKillStockAtomicLong.get();
        if(stock<=0){
            log.error("该活动商品已无库存");
            return;
        }
        //2判断是用户否正在抢购
        RBoundedBlockingQueue<Object> secKillUserQueue = RedissonUtils.getRBoundedBlockingQueue("secKill-user-" + secKillDto.getActivityId());
        boolean contains = secKillUserQueue.contains(secKillDto.getUserId());
        if(contains){
            log.error("该用户{}正在抢购中",secKillDto.getUserId());
            return;
        }
        //3判断用户是否已抢购成功
        RMap<Object, Object> secKillSuccessRMap = RedissonUtils.getMap("secKill-success-"+secKillDto.getActivityId());
        boolean containsSecKillSuccessUser = secKillSuccessRMap.containsKey(secKillDto.getUserId());
        if(containsSecKillSuccessUser){
            log.error("该用户{}已经抢购成功,只能抢购一次",secKillDto.getUserId());
            return;
        }
        //4判断是否有库存
        RAtomicLong secKillStockAtomicLong2 = RedissonUtils.getAtomicLong("secKill-stock-" + secKillDto.getActivityId());
        long stock2 = secKillStockAtomicLong2.get();
        if(stock2<=0){
            log.error("该活动商品已无库存");
            secKillUserQueue.clear();
            return;
        }
        //用户进行抢购
        boolean offer = secKillUserQueue.offer(secKillDto.getUserId());
        if(!offer){
            log.error("该用户{}抢购失败",secKillDto.getUserId());
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
        //移除正在抢购队列中的用户
//        secKillUserQueue.poll();
        //8用户抢购成功---生成订单ID
        String orderId="orderId";
        Map<String,Object> map=new HashMap<>();
        map.put(orderId,secKillDto.getActivityId());
        secKillSuccessRMap.fastPut(secKillDto.getUserId(),map);
        log.info("用户{}抢购成功",secKillDto.getUserId());
        //9订单消息发送订单服务
        log.info(secKillDto.getUserId()+"秒杀成功，并发送mq消息到订单服务生成订单");
    }
    @Override
    public void asyncExecuteSecKill(SecKillDto secKillDto) {
        log.info("用户{},正在执行秒杀活动",secKillDto.getUserId());
        //1判断是否在抢购期内
        //1.1首先判断库存
        RAtomicLong secKillStockAtomicLong = RedissonUtils.getAtomicLong("secKill-stock-" + secKillDto.getActivityId());
        long stock = secKillStockAtomicLong.get();
        if(stock<=0){
            log.info("该活动商品已无库存");
            return;
        }
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
        RAtomicLong secKillStockAtomicLong2 = RedissonUtils.getAtomicLong("secKill-stock-" + secKillDto.getActivityId());
        long stock2 = secKillStockAtomicLong2.get();
        if(stock2<=0){
            //清空抢购进行中set
            secKillingSet.clear();
            log.info(secKillDto.getUserId()+"参加该活动，商品已无库存");
            return;
        }
        //5 异步执行秒杀
        try {
            asyncSecKillService.asyncSecKill(secKillDto);
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
        //初始化活动信息
        RMap<Object, Object> activityMap = RedissonUtils.getMap("secKill-activity-" + secKillInitDto.getActivityId());
        Map<Object, Object> map = new HashMap<>();
        long startTime = DateUtil.parseDateTime(secKillInitDto.getStartTime()).getTime();
        long endTime = DateUtil.parseDateTime(secKillInitDto.getEndTime()).getTime();
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        activityMap.putAll(map);
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
