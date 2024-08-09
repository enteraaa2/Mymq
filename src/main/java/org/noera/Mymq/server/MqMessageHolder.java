package org.noera.Mymq.server;

import org.noear.socketd.transport.core.Message;

/**
 * @program: Mymq
 * @description: 消息持有人，用作管理和跟踪消息的状态，
 * 以便在消息队列系统中实现延迟重试或重新派发功能
 * @author: lydms
 * @create: 2024-08-09 22:42
 **/
public class MqMessageHolder {
    //保存一个Message对象，表示当前持有的消息。
    private Message message;
    //nextTime：记录下次派发消息的时间（以毫秒为单位）。
    private long nextTime;
    //times：跟踪消息已经被派发的次数。
    private int times;
    public MqMessageHolder(Message message) {
        this.message = message;
    }
    /**
     * 获取消息
     * @return
     */
    public Message getMessage(){
        return message;
    }

    /**
     * 获取下次队列派发消息的时间（单位：毫秒）
     * @return
     */
    public long getNextTime(){
        return nextTime;
    }


    /**
     * 获取派发次数
     * @return
     */
    public int getTimes(){
        return times;
    }

    /**
     * 延后(生成下次派发时间)方法增加派发次数，并计算下次派发的时间。
     */
    public MqMessageHolder deferred(){
        times++;
        nextTime=MqNextTime.getNextTime(this);
        return this;
    }
}
