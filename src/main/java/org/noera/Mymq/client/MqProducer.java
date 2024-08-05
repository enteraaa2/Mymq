package org.noera.Mymq.client;

import java.io.IOException;

/**
 * @program: Mymq
 * @description: 生产者
 * @author: lydms
 * @create: 2024-08-03 18:06
 **/
public interface MqProducer {
    /**
    * @Description: 发送
    * @Param: [topic, message]主题，消息
    * @return: [java.lang.String, java.lang.String]
    * @Author: lydms
    * @Date: 2024/8/3
    */
    void publish(String topic,String message) throws IOException;
}
