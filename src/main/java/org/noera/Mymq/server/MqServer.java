package org.noera.Mymq.server;

/**
 * @program: Mymq
 * @description: 服务端
 * @author: lydms
 * @create: 2024-08-03 18:36
 **/

public interface MqServer {
    void start(int port) throws Exception;
    void stop();
}
