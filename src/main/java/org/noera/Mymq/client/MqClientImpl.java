package org.noera.Mymq.client;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.BuilderListener;
import org.noera.Mymq.MqConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @program: Mymq
 * @description: 客户端实例
 * @author: lydms
 * @create: 2024-08-03 18:06
 **/

public class MqClientImpl extends BuilderListener implements MqClient {
    private String serverUrl;
    private Session session;
    private Map<String, MqConsumerHandler> subscribeMap = new HashMap<>();

    public MqClientImpl(String serverUrl) throws Exception {
        this.serverUrl = serverUrl.replace("Mymq://","sd:tcp://");
        this.session = SocketD.createClient(this.serverUrl)
                .listen(this)
                .open();
        on(MqConstants.MQ_CMD_DISTRIBUTE,(s,m)->{
            String topic = m.meta(MqConstants.MQ_TOPIC);
            onDistribute(topic,m.dataAsString());
        });
    }

    /**
     * 订阅
     *
     * @param topic
     * @param handler
     * @throws IOException
     */

    @Override
    public CompletableFuture<?> subscribe(String topic, MqConsumerHandler handler) throws IOException {
        //Qos0：消息可能不会被发送，或者发送后没有确认。
        //Qos1：消息会被发送，并且会有一个基本的确认机制，确保消息至少被发送一次。
        //Qos2：消息会被发送，并且会有更可靠的确认机制，确保消息只被发送一次，并且被正确接收。
        //支持Qos1
        subscribeMap.put(topic, handler);
        //send->要求返回，可以知道到底订阅了没有
        //session.sendAndRequest(MqConstants.MQ_CMD_SUBSCRIBE, new StringEntity("").meta(MqConstants.MQ_TOPIC, topic));
        CompletableFuture<?> future=new CompletableFuture<>();
        //订阅接口有回调
        session.sendAndSubscribe(MqConstants.MQ_CMD_SUBSCRIBE, new StringEntity("").meta(MqConstants.MQ_TOPIC, topic),(r)->{
            future.complete(null);
        });
        return future;
    }

    /**
     * 发布
     *
     * @param topic
     * @param message
     * @throws IOException
     */
    @Override
    public CompletableFuture<?> publish(String topic, String message) throws IOException {
        //Qos0：消息可能不会被发送，或者发送后没有确认。
        //Qos1：消息会被发送，并且会有一个基本的确认机制，确保消息至少被发送一次。
        //Qos2：消息会被发送，并且会有更可靠的确认机制，确保消息只被发送一次，并且被正确接收。
        //支持Qos1
        //session.sendAndRequest(MqConstants.MQ_CMD_PUBLISH, new StringEntity(message).meta(MqConstants.MQ_TOPIC, topic));
        CompletableFuture<?> future=new CompletableFuture<>();
        //订阅接口有回调
        session.sendAndSubscribe(MqConstants.MQ_CMD_PUBLISH, new StringEntity(message).meta(MqConstants.MQ_TOPIC, topic),(r)->{
            future.complete(null);
        });
        return future;
    }

    /**
     * 当派发时
     * @param topic
     * @param message
     * @throws IOException
     */
    public void onDistribute(String topic,String message) throws IOException {
        MqConsumerHandler handler=subscribeMap.get(topic);
        if(handler != null) {
            handler.handle(topic,message);
        }
    }
}
