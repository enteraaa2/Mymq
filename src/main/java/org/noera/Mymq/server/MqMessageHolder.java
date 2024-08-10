package org.noera.Mymq.server;

import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.entity.StringEntity;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;

/**
 * @program: Mymq
 * @description: 消息持有人，用作管理和跟踪消息的状态，
 * 以便在消息队列系统中实现延迟重试或重新派发功能
 * @author: lydms
 * @create: 2024-08-09 22:42
 **/
public class MqMessageHolder {
    //保存一个Message对象，表示当前持有的消息。
    //表示发过来的消息
    private Message from;
    //nextTime：记录下次派发消息的时间（以毫秒为单位）。
    private long nextTime;
    //times：跟踪消息已经被派发的次数。
    private int times;
    //可以将message对象中的数据内容转换成统一的格式，可以对元数据进行管理
    private EntityDefault content;
    //在MqMessageHolder 中用于管理消息的延迟处理任务，提高了任务的状态
    //提供了任务的状态管理和取消能力，帮助实现消息的调度与重试机制。
    //想着定义延时碎裂来着，现在直接用deferredFuture 可以用于管理这个延迟任务。
    protected ScheduledFuture<?> deferredFuture;
    public MqMessageHolder(Message from) {
        this.from = from;
        //先进行格式定义为字符类型，然后将message中的元数据复制到content中
        this.content=new StringEntity(from.dataAsString()).metaMap(from.metaMap());
    }
    /**
     * 获取来源消息
     * @return
     */
    //    public Message getFrom(){
    //        return from;
    //    }

    /**
     * 获取消息内容
     * @return
     */
    public EntityDefault getContent() throws IOException {
        //防止重发
        //调用 content.data().reset() 方法,可以确保每次读取消息时都从头开始
        //,避免因读取位置不正确而导致的问题。这有助于提高系统的性能和稳定性。
        content.data().reset();
        return content;
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