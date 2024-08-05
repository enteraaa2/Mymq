package org.noera.Mymq.client;
/**
 * @program: Mymq
 * @description: 消费者处理器
 * @author: lydms
 * @create: 2024-08-03 18:06
 **/
public interface MqConsumerHandler {
    /**
     * 处理
     * @param topic
     * @param message
     */
    void handle(String topic,String message);
}
