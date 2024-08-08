package org.noera.Mymq.client;

import org.noear.socketd.transport.core.Message;
import org.noera.Mymq.MqConstants;

import java.io.IOException;

/**
 * @program: Mymq
 * @description: 消息结构体实现
 * @author: lydms
 * @create: 2024-08-08 21:33
 **/

public class MqMessageImpl implements MqMessage {
    private final MqClientInternal clientInternal;
    private final Message message;
    private final String content;
    private final int times;
    public MqMessageImpl(MqClientInternal clientInternal, Message message) {
        //客户端的封装对象客户端内部对象，创建时将与之消息结构体绑定
        this.clientInternal = clientInternal;
        this.message = message;
        this.content=message.dataAsString();
        //默认为0次
        this.times=Integer.parseInt(message.metaOrDefault(MqConstants.MQ_TIMES,"0"));
    }

    @Override
    public String getKey() {
        return message.sid();
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public int getTimes() {
        return times;
    }
    //手动ack其底层实现的就是内部的ack
    @Override
    public void acknowledge(boolean isOk) throws IOException {
        clientInternal.acknowledge(message,isOk);
    }

    @Override
    public String toString() {
        return getContent();
    }
}
