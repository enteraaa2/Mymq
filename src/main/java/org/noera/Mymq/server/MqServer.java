package org.noera.Mymq.server;

/**
 * @program: Mymq
 * @description: 服务端
 * @author: lydms
 * @create: 2024-08-03 18:36
 **/

public interface MqServer {
    MqServer addAccess(String accessKey, String accessSecretKey);
    MqServer start(int port) throws Exception;
    MqServer stop();
}
