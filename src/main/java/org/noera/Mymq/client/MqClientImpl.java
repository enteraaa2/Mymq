package org.noera.Mymq.client;


import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.BuilderListener;
import org.noera.Mymq.MqConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class MqClientImpl extends BuilderListener implements MqClientInternal {
    //消息消费失败打印日志
    private static final Logger log= LoggerFactory.getLogger(MqClientImpl.class);

    private String serverUrl;
    private Session session;
    private Map<String, MqConsumerHandler> subscribeMap = new HashMap<>();
    //增加自动ack默认值
    private boolean autoAck = true;
    public MqClientImpl(String serverUrl) throws Exception {
        this.serverUrl = serverUrl.replace("Mymq://","sd:tcp://");
        this.session = SocketD.createClient(this.serverUrl)
                .listen(this)
                .open();
        /**
         * 接受派发指令
         */
        on(MqConstants.MQ_CMD_DISTRIBUTE,(s,m)->{
            String topic = m.meta(MqConstants.MQ_TOPIC);
            try{
                //在客户端创建的时候，接受派发指令，当在派发消息时会判断是否在创建客户端
                //的时候开启了ack确认，如果开启了就直接调用内部客户端的ack确认方法为全局true
                //如果没有开启，则正常消费，如果消费失败，就会进入到catch块中将该消息设为false
                onDistribute(topic,m);
                //是否开启自动ack确认机制
                if (autoAck){

                    acknowledge(m,true);
                }
            }catch(Exception ex){
                acknowledge(m,false);
            }
        });
    }

    /**
     * 订阅消息
     *
     * @param topic
     * @param subscription
     * @throws IOException
     */

    @Override
    public CompletableFuture<?> subscribe(String topic, Subscription subscription) throws IOException {
        //Qos0：消息可能不会被发送，或者发送后没有确认。
        //Qos1：消息会被发送，并且会有一个基本的确认机制，确保消息至少被发送一次。
        //Qos2：消息会被发送，并且会有更可靠的确认机制，确保消息只被发送一次，并且被正确接收。
        //支持Qos1
        subscribeMap.put(topic, subscription.getHandler());

        Entity entity=new StringEntity("")
                .meta(MqConstants.MQ_TOPIC, topic)
                .meta(MqConstants.MQ_IDENTITY, subscription.getIdentity());

        //send->要求返回，可以知道到底订阅了没有
        //session.sendAndRequest(MqConstants.MQ_CMD_SUBSCRIBE, new StringEntity("").meta(MqConstants.MQ_TOPIC, topic));
        CompletableFuture<?> future=new CompletableFuture<>();
        //订阅接口有回调
        session.sendAndSubscribe(MqConstants.MQ_CMD_SUBSCRIBE, entity,(r)->{
            future.complete(null);
        });
        return future;
    }

    /**
     * 发布消息
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
     * 自动ack
     * @param autoAck
     * @return
     */
    @Override
    public MqClient autoAck(boolean autoAck) {
        this.autoAck=autoAck;
        return this;
    }
    /**
     * 消费确认
     * @param message
     * @param isOk
     * @throws IOException
     */
    @Override
    public void acknowledge(Message message,boolean isOk)throws IOException {
        //内部手动ack重写，手动确认消息的接收状态。根据处理结果，向服务器发送确认或拒绝信号
        session.replyEnd(message,new StringEntity("").meta(MqConstants.MQ_ACK,isOk?"1":"0"));
    }

    /**
     * 当派发时
     * @param topic
     * @param message
     * @throws IOException
     */
    public void onDistribute(String topic, Message message) throws IOException {
        MqConsumerHandler handler=subscribeMap.get(topic);
        if(handler != null) {
            //将此处的消息处理器接收参数改为消息实现
            handler.handle(topic,new MqMessageImpl(this,message));
        }
    }

    /**
     * 当消息消费失败时
     * @param session
     * @param error
     */
    @Override
    public void onError(Session session, Throwable error) {
        super.onError(session, error);
        //当消息消费失败时候就会自动调用该方法打印日志，处理更多逻辑
        if(log.isWarnEnabled()){
            log.warn("{}",error);
        }
    }

}
