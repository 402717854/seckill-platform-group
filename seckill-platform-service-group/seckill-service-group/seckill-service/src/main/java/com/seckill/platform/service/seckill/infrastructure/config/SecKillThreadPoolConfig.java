package com.seckill.platform.service.seckill.infrastructure.config;

import com.seckill.platform.service.seckill.infrastructure.config.support.PlatformThreadPoolTaskExecutor;
import com.seckill.platform.service.seckill.infrastructure.config.support.SecKillAbortPolicy;
import com.seckill.platform.service.seckill.infrastructure.config.support.ThreadPoolProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 自定义带监控的线程池
 * @author wys
 */
@Configuration
@EnableConfigurationProperties({ ThreadPoolProperties.class })
@EnableAsync
public class SecKillThreadPoolConfig {

    @Bean(name="secKillThreadPool")
    public TaskExecutor taskExecutor(ThreadPoolProperties threadPoolProperties) {
        PlatformThreadPoolTaskExecutor executor = new PlatformThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        // 设置最大线程数
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        // 设置队列容量
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        //线程空闲后最大存活时间
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        // 设置默认线程名称
        executor.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());
        // 设置拒绝策略rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new SecKillAbortPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);

        return executor;
    }

}
