package org.noera.Mymq.server;

/**
 * @program: Mymq
 * @description: 消息队列
 * @author: lydms
 * @create: 2024-08-09 22:45
 **/
public interface MqMessageQueue {
    /**
     * 获取关联身份，将该消息队列和身份客户端进行绑定，
     *你想啊肯定是自己消费自己的消息
     * @return
     */
    String getIdentity();
    /**
     * 添加消息
     * @param mqMessageHolder
     */
    void add(MqMessageHolder mqMessageHolder);
}
