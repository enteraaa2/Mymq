package org.noera.Mymq.server;

import org.noear.socketd.transport.core.Session;
import org.noear.socketd.utils.RunUtils;
import org.noera.Mymq.MqConstants;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: Mymq
 * @description: 消息队列实现
 * @author: lydms
 * @create: 2024-08-09 22:48
 **/

public class MqMessageQueueImpl implements MqMessageQueue {
    //队列，用于存储待处理的消息
    private final Queue<MqMessageHolder> queue = new LinkedList<MqMessageHolder>();
    //    //延后队列（死信队列），用于存储需要重试的消息
    //删除延时队列，增加deferredFuture做延时即可
    //    private final Queue<MqMessageHolder> delayedQueue = new LinkedList<MqMessageHolder>();
    //身份，标识当前队列所属的用户或应用
    private final String identity;
    //会话引用，用于分发消息
    private final Set<Session> sessionSet;

    /**
     * 实例化的时候一个队列关联一个用户身份
     */
    public MqMessageQueueImpl(String identity, Set<Session> sessionSet) {
        this.sessionSet = sessionSet;
        this.identity = identity;
    }

    /**
     * 获取关联身份
     *
     * @return
     */
    @Override
    public String getIdentity() {
        return identity;
    }

    /**
     * 添加消息，之后就可以马上开始（异步）分发消息了
     * 毕竟队列里边有消息了，就可以分发去消费了
     *
     * @param mqMessageHolder
     */
    @Override
    public void add(MqMessageHolder mqMessageHolder) {
        queue.add(mqMessageHolder);
        //在这里可能会改成线程异步
        distribute();
    }
    //将消息设置延时任务，并在延后多长时间后将消息重新添加到队列中
    //功能就是延时队列
    public void addDelayed(MqMessageHolder mqMessageHolder) {
        //首先判断当前消息调度者如果已经有一个定时任务正在运行了，
        //先取消它，避免重复调度。
        if (mqMessageHolder.deferredFuture != null) {
            mqMessageHolder.deferredFuture.cancel(true);
        }
        //使用 RunUtils.delay 方法创建一个新的延迟任务，
        //这个任务会在指定的延迟时间后重新调用 add(mqMessageHolder) 方法。
        //延迟时间通过 mqMessageHolder.getNextTime() - System.currentTimeMillis() 计算得出，
        //确保消息在适当的时间被重新添加到队列中。
        mqMessageHolder.deferredFuture = RunUtils.delay(() -> {
            add(mqMessageHolder);
        }, mqMessageHolder.getNextTime() - System.currentTimeMillis());
    }


    //派发
    private void distribute() {
        //找到此身份的其中一个会话（如果是ip就一个，如果是集群就任选一个）
        //使用parallelStream做并发处理
        List<Session> sessions = sessionSet.parallelStream()
                .filter(s -> s.attrMap().containsKey(identity))
                .collect(Collectors.toList());
        if (sessions.size() > 0) {

            MqMessageHolder mqMessageHolder;
            //这里表示一个死循环一直去取消息，直到队列里边无消息可消费为止
            while (true) {
                mqMessageHolder = queue.poll();
                if (mqMessageHolder == null) {
                    break;
                }
                //如果当前时间已经超过去派发时间了，那么就放入延后队列
                if (!MqNextTime.allowDistribute(mqMessageHolder)) {
                    //现将消息进行延迟然后机制，增加派发次数并计算下次派发时间
                    //然后进入到延后队列，之后再试
                    //将延时队列转为deferredFuture
                    addDelayed(mqMessageHolder.deferred());
                    continue;
                }

                try {
                    distributeDo(mqMessageHolder, sessions);
                } catch (Exception e) {
                    //现将消息进行延迟然后机制，增加派发次数并计算下次派发时间
                    //然后进入到延后队列，之后再试
                    //将延时队列转为deferredFuture
                    addDelayed(mqMessageHolder.deferred());
                }
            }
        }
    }

    //派发处理
    private void distributeDo(MqMessageHolder mqMessageHolder, List<Session> sessions) throws IOException {
        //随机取一个会话
        int idx = 0;
        if (sessions.size() > 1) {
            idx = new Random().nextInt(sessions.size());
        }
        Session s1 = sessions.get(idx);
        //TODO：这里可能会有线程同步问题
        //在消息的元数据中记录当前的派发次数，以便在消息发送和处理过程中进行跟踪和管理。
        mqMessageHolder.getContent().meta(MqConstants.MQ_TIMES, String.valueOf(mqMessageHolder.getTimes()));
        //给会话发送消息，确认机制ack
        s1.sendAndSubscribe(MqConstants.MQ_CMD_DISTRIBUTE, mqMessageHolder.getContent(), (m) -> {
            int ack = Integer.parseInt(m.metaOrDefault(MqConstants.MQ_ACK, "0"));
            //当ack为0的时候，证明派发失败，放入到延时队列中等待下一次派发
            if (ack == 0) {
                //现将消息进行延迟然后机制，增加派发次数并计算下次派发时间
                //然后进入到延后队列，之后再试//TODO：如果因为网络问题，没有回调怎么办？
                //将延时队列转为deferredFuture
                addDelayed(mqMessageHolder.deferred());
            }
        });
    }
}