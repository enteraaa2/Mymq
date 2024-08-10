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
    //private final Message message;
    //发过来的消息
    private final Message from;
    //消息主题
    private final String topic;
    private final String content;
    private final int times;
    public MqMessageImpl(MqClientInternal clientInternal, Message from) {
        //客户端的封装对象客户端内部对象，创建时将与之消息结构体绑定
        this.from=from;
        this.clientInternal = clientInternal;
        this.topic = from.metaOrDefault(MqConstants.MQ_TOPIC,"");
        this.content=from.dataAsString();
        //默认为0次
        this.times=Integer.parseInt(from.metaOrDefault(MqConstants.MQ_TIMES,"0"));
    }
    //
    //    @Override
    //    public String getKey() {
    //        return message.sid();
    //    }
    //得到消息主题
    public String getTopic() {
        return topic;
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
        clientInternal.acknowledge(from,isOk);
    }
    //提供消息的字符串表示，包含主题、派发次数和内容，便于调试和日志记录。
    @Override
    public String toString() {
        return "MqMessage{" +
                "topic='" + topic + '\'' +
                ", times=" + times +
                ", content='" + content + '\'' +
                '}';
    }
}