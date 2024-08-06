package org.noera.Mymq.client;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @program: Mymq
 * @description: 消费者
 * @author: lydms
 * @create: 2024-08-03 18:06
 **/
public interface MqConsumer {
    /**
     * @Description:订阅
     * @Param: [topic, subscription]主题，订阅
     * @return: [java.lang.String, org.noera.Mymq.client.ConsumerHandler]
     * @Author: lydms
     * @Date: 2024/8/3
     */
    //同步等待接口void性能较差--->换成响应式接口CompletableFuture
    //subscription-->订阅者（身份+消息处理器）
    CompletableFuture<?> subscribe(String topic, Subscription  subscription) throws IOException;

}
