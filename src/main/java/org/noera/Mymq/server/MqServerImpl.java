package org.noera.Mymq.server;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.BuilderListener;
import org.noear.socketd.transport.server.Server;
import org.noera.Mymq.MqConstants;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: Mymq
 * @description: 服务端实例
 * @author: lydms
 * @create: 2024-08-03 18:51
 **/

public class MqServerImpl extends BuilderListener implements MqServer {
    private Server server;
    //保存session集合
    private Set<Session> sessionSet = new HashSet<>();
    //主题与身份集合
    private Map<String, Set<String>> subscribeMap = new HashMap<>();
    //身份校验集合
    private Map<String, String> accessMap = new HashMap<>();

    @Override
    public MqServer addAccess(String accessKey, String accessSecretKey) {
        accessMap.put(accessKey, accessSecretKey);
        return this;
    }

    //当一个会话开始时候
    @Override
    public void onOpen(Session session) throws IOException {
        super.onOpen(session);
        if (accessMap.size() > 0) {
            String accessKey = session.param(MqConstants.PARAM_ACCESS_KEY);
            String accessSecretKey = session.param(MqConstants.PARAM_ACCESS_SECRET_KEY);

            if (accessKey == null && accessSecretKey == null) {
                session.close();
                return;
            }

            if (accessSecretKey.equals(accessMap.get(accessKey)) == false) {
                session.close();
                return;
            }
        }
        //添加当前会话session
        sessionSet.add(session);
    }

    //当一个会话结束时
    @Override
    public void onClose(Session session) {
        //删除当前会话session
        sessionSet.remove(session);
        super.onClose(session);

    }

    @Override
    public MqServer start(int port) throws Exception {
        server = SocketD.createServer("sd:tcp")
                .config(c -> c.port(port))
                .listen(this)
                .start();
        //接收订阅指令
        on(MqConstants.MQ_CMD_SUBSCRIBE, ((s, m) -> {
            //收到之后马上交给他
            if (m.isRequest() || m.isSubscribe()) {
                s.replyEnd(m, new StringEntity(""));
            }
            String topic = m.meta(MqConstants.MQ_TOPIC);
            String identity = m.meta(MqConstants.MQ_IDENTITY);
            onSubscribe(topic, identity, s);

        }));
        //接收发布指令
        on(MqConstants.MQ_CMD_PUBLISH, ((s, m) -> {
            //收到之后马上交给他
            if (m.isRequest() || m.isSubscribe()) {
                s.replyEnd(m, new StringEntity(""));
            }
            String topic = m.meta(MqConstants.MQ_TOPIC);
            onPublish(topic, m);
        }));
        return this;
    }

    @Override
    public MqServer stop() {
        server.stop();
        return this;
    }

    /**
     * 当订阅时
     *
     * @param topic
     * @param session
     */
    private synchronized void onSubscribe(String topic, String identity, Session session) {
        //给会话添加身份（可以处理多个不同的身份）
        session.attr(identity, "1");
        //首先先得往当前session中加入身份identity，将当前会话与身份关联起来
        //以身份进行订阅，这里有可能有多个身份
        Set<String> identitys = subscribeMap.get(topic);
        if (identitys == null) {
            identitys = new LinkedHashSet<>();
            subscribeMap.put(topic, identitys);
        }
        //往当前主题下去添加当前身份例如在demo主题下的身份a和身份b
        identitys.add(identity);
    }

    /**
     * 当发布时
     *
     * @param topic
     * @param message
     * @throws IOException
     */
    private void onPublish(String topic, Message message) throws IOException {
        //获取当前主题下的说有身份
        Set<String> identitys = subscribeMap.get(topic);
        if (identitys != null) {
            for (String identity : identitys) {
                //找到此身份的其中一个会话（如果是ip就一个，如果是集群就任选一个）
                //使用parallelStream做并发处理
                List<Session> sessions = sessionSet.parallelStream()
                        .filter(s -> s.attrMap().containsKey(identity))
                        .collect(Collectors.toList());
                if (sessions.size() > 0) {
                    //随机取一个会话
                    int idx = 0;
                    if (sessions.size() > 1) {
                        idx = new Random().nextInt(sessions.size());
                    }
                    Session s1 = sessions.get(idx);

                    //给会话发送消息
                    s1.send(MqConstants.MQ_CMD_DISTRIBUTE, message);
                    message.data().reset();
                }
            }
        }
    }


}
