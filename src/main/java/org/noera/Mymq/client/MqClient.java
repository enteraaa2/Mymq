package org.noera.Mymq.client;

/**
 * @program: Mymq
 * @description: 客户端接口
 * @author: lydms
 * @create: 2024-08-03 18:06
 **/
public interface MqClient extends MqConsumer, MqProducer {
    /**
     * 是否自动ack确认,  在创建客户端的时候就能直接确认是否自动ack
     * @param autoAck
     */
    MqClient autoAck(boolean autoAck);
}
