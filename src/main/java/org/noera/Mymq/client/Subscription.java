package org.noera.Mymq.client;

/**
 * @program: Mymq
 * @description: 订阅者
 * @author: lydms
 * @create: 2024-08-06 21:26
 **/

public class Subscription {
    private String identity;
    private MqConsumerHandler handler;
    public Subscription(String identity, MqConsumerHandler handler) {
        this.identity = identity;
        this.handler = handler;
    }
    public String getIdentity() {
        return identity;
    }
    public MqConsumerHandler getHandler() {
        return handler;
    }
}
