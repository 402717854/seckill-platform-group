package com.seckill.platform.service.seckill.infrastructure.config.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @author wys
 * 线程池监控
 */
public class ThreadPoolMonitor extends ThreadPoolExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolMonitor.class);

    private ConcurrentHashMap<String, Date> startTimes;

    /**
     * 调用父类的构造方法，并初始化HashMap和线程池名称
     *
     * @param corePoolSize    线程池核心线程数
     * @param maximumPoolSize 线程池最大线程数
     * @param keepAliveTime   线程的最大空闲时间
     * @param unit            空闲时间的单位
     * @param workQueue       保存被提交任务的队列
     * @param threadFactory   线程工厂
     * @param handler         拒绝策略
     */
    public ThreadPoolMonitor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                             TimeUnit unit, BlockingQueue<Runnable> workQueue,
                             ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.startTimes = new ConcurrentHashMap<>();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        startTimes.put(String.valueOf(r.hashCode()), new Date());
    }
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        Date startDate = startTimes.remove(String.valueOf(r.hashCode()));
        Date finishDate = new Date();
        long diff = finishDate.getTime() - startDate.getTime();
        LOGGER.info("当前线程执行情况:" +
                        "执行当前线程耗时Duration: {} ms, 初始线程数PoolSize: {}, 最大执行线程数LargestPoolSize: {},活跃线程数Active: {}, " +
                        "执行完成任务数Completed: {}, 执行任务总数Task: {}, 队列缓存数Queue: {}, " +
                        "MaximumPoolSize: {}, CorePoolSize: {}, KeepAliveTime: {}, isShutdown: {}, isTerminated: {}",
                diff, this.getPoolSize(),this.getLargestPoolSize(), this.getActiveCount(),
                this.getCompletedTaskCount(), this.getTaskCount(), this.getQueue().size(),
                this.getMaximumPoolSize(),this.getCorePoolSize(),this.getKeepAliveTime(TimeUnit.MILLISECONDS), this.isShutdown(), this.isTerminated());
    }

}
