package com.seckill.platform.search.canal;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.Message;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 从canal消息队列获取变更数据
 * @author lenovo
 */
@Component
public class CanalMqConsumer {

    private Logger logger = LoggerFactory.getLogger(CanalMqConsumer.class);


    @StreamListener(CanalSink.CANAL_INPUT)
    public void onCanalMessage(@Payload Map<String,Object> message) {
        logger.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), JSON.toJSON(message));
        boolean isDdl = (boolean) message.get("isDdl");
        //不处理DDL事件
        if(isDdl){
            return;
        }
        // TiCDC的id，应该具有唯一性，先保存再说
        int tid = (int) message.get("id");
        // TiCDC生成该消息的时间戳，13位毫秒级
        long ts = (long) message.get("ts");
        // 数据库
        String database = (String) message.get("database");
        // 表
        String table = (String) message.get("table");
        // 类型：INSERT/UPDATE/DELETE
        String type = (String) message.get("type");
        // 每一列的数据值
        List<?> data = (List<?>) message.get("data");
        // 仅当type为UPDATE时才有值，记录每一列的名字和UPDATE之前的数据值
        List<?> old = (List<?>) message.get("old");
        // 跳过sys_backup，防止无限循环
        if ("sys_backup".equalsIgnoreCase(table)) {
            return;
        }
        // 只处理指定类型
        if (!"INSERT".equalsIgnoreCase(type)
                && !"UPDATE".equalsIgnoreCase(type)
                && !"DELETE".equalsIgnoreCase(type)) {
            return;
        }
        // <1> 注意，此处抛出一个 RuntimeException 异常，模拟消费失败
//        throw new RuntimeException("我就是故意抛出一个异常");
    }

    @ServiceActivator(inputChannel = "example.canal-consumer-group-example.errors")
    public void handleError(ErrorMessage errorMessage) {
        logger.error("[handleError][payload：{}]", ExceptionUtils.getRootCauseMessage(errorMessage.getPayload()));
        logger.error("[handleError][originalMessage：{}]", JSON.toJSON(errorMessage.getOriginalMessage()));
        logger.error("[handleError][headers：{}]", errorMessage.getHeaders());
    }
}
