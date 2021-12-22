package com.seckill.platform.service.seckill.infrastructure.support.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 秒杀线程池拒绝策略
 * @author wys
 */
@Slf4j
public class SecKillAbortPolicy implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        int taskingCount=executor.getActiveCount()+executor.getQueue().size();
        throw new RejectedExecutionException("当前正在抢购的人数为:"+taskingCount+",请稍后再试");
    }
}
