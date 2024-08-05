package org.noera.Mymq.client;

import java.io.IOException;

/**
 * @program: Mymq
 * @description: 消费者
 * @author: lydms
 * @create: 2024-08-03 18:06
 **/
public interface MqConsumer {
    /**
     * @Description:订阅
     * @Param: [topic, handler]主题，消费处理
     * @return: [java.lang.String, org.noera.Mymq.client.ConsumerHandler]
     * @Author: lydms
     * @Date: 2024/8/3
     */
    void subscribe(String topic, MqConsumerHandler handler) throws IOException;

}
