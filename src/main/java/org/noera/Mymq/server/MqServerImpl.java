package org.noera.Mymq.server;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.BuilderListener;
import org.noear.socketd.transport.server.Server;
import org.noera.Mymq.MqConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @program: Mymq
 * @description: 服务端实例
 * @author: lydms
 * @create: 2024-08-03 18:51
 **/

public class MqServerImpl extends BuilderListener implements MqServer {
    private Server server;
    private Map<String, Set<Session>> subscribeMap = new HashMap<>();

    @Override
    public void start(int port) throws Exception {
        server = SocketD.createServer("sd:tcp")
                .config(c -> c.port(port))
                .listen(this)
                .start();
        //接收订阅指令
        on(MqConstants.MQ_CMD_SUBSCRIBE, ((s, m) -> {
            //收到之后马上交给他
            if(m.isRequest()||m.isSubscribe()){
                s.replyEnd(m,new StringEntity(""));
            }
            String topic = m.meta(MqConstants.MQ_TOPIC);
            onSubscribe(topic, s);

        }));
        //接收发布指令
        on(MqConstants.MQ_CMD_PUBLISH, ((s, m) -> {
            //收到之后马上交给他
            if(m.isRequest()||m.isSubscribe()){
                s.replyEnd(m,new StringEntity(""));
            }
            String topic = m.meta(MqConstants.MQ_TOPIC);
            onPublish(topic, m);
        }));
    }

    /**
     * 当订阅时
     * @param topic
     * @param session
     */
    private synchronized void onSubscribe(String topic, Session session) {
        Set<Session> sessions = subscribeMap.get(topic);
        if (sessions == null) {
            sessions = new LinkedHashSet<>();
            subscribeMap.put(topic, sessions);
        }

        sessions.add(session);
    }

    /**
     * 当发布时
     * @param topic
     * @param message
     * @throws IOException
     */
    private void onPublish(String topic, Message message) throws IOException {
        //取消所有订阅的会话
        Set<Session> sessions = subscribeMap.get(topic);
        if (sessions != null) {
            for (Session s : sessions) {
                s.send(MqConstants.MQ_CMD_DISTRIBUTE, message);
                message.data().reset();
            }
        }
    }

    @Override
    public void stop() {
        server.stop();
    }
}
