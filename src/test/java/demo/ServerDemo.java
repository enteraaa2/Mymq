package demo;

import org.noera.Mymq.server.MqServer;
import org.noera.Mymq.server.MqServerImpl;

/**
 * @program: Mymq
 * @description: 服务端测试例
 * @author: lydms
 * @create: 2024-08-03 19:06
 **/

public class ServerDemo {
    public static void main(String[] args) throws Exception {
        MqServer server = new MqServerImpl();
        server.start(9393);
    }
}
