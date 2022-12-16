package com.seckill.platform.search.canal;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CanalSink {
    String CANAL_INPUT = "canal-input";

    @Input(CANAL_INPUT)
    SubscribableChannel canalInput();
}
