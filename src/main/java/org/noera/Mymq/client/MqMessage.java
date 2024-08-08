package org.noera.Mymq.client;

import java.io.IOException;

/**
 * @program: Mymq
 * @description: 消息结构体定义
 * @author: lydms
 * @create: 2024-08-08 21:28
 **/
public interface MqMessage {
    /**
     * 消息键
     * @return
     */
    String getKey();
    /**
     * 消息内容
     * @return
     */
    String getContent();

    /**
     * 已派发次数
     * @return
     */
    int getTimes();

    /**
     * 确认
     * @param isOk
     */
    void acknowledge(boolean isOk)throws IOException;
}
