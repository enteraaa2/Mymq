package org.noera.Mymq;

/**
 * @program: Mymq
 * @description: 常量接口
 * @author: lydms
 * @create: 2024-08-03 18:17
 **/
public interface MqConstants {
    String MQ_TOPIC="mq.topic";
    String MQ_IDENTITY="mq.identity";
    String MQ_TIMES="mq.times";
    String MQ_ACK="mq.ack";

    String MQ_CMD_SUBSCRIBE="mq.cmd.subscribe";
    String MQ_CMD_PUBLISH ="mq.cmd.publish";
    String MQ_CMD_DISTRIBUTE ="mq.cmd.distribute";

    String PARAM_ACCESS_KEY="accessKey";
    String PARAM_ACCESS_SECRET_KEY="accessSecretKey";
}
