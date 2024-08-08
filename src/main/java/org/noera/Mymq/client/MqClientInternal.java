package org.noera.Mymq.client;

import org.noear.socketd.transport.core.Message;

import java.io.IOException;

/**
 * @program: Mymq
 * @description: 客户端内部扩展处理接口
 * @author: lydms
 * @create: 2024-08-08 21:40
 **/

public interface MqClientInternal extends MqClient{
    /**
     * 内部手动确认ack。在代码中代用的ack，并不直接向外不开放，开放的手动ack底层调用它
     * @param message
     */
    void acknowledge(Message message,boolean isOk)throws IOException;
}
