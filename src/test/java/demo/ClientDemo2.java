package demo;

import org.noera.Mymq.client.MqClient;
import org.noera.Mymq.client.MqClientImpl;

/**
 * @program: Mymq
 * @description: 客户端测试例
 * @author: lydms
 * @create: 2024-08-03 18:36
 **/

public class ClientDemo2 {
    public static void main(String[] args) throws Exception {
        //客户端
        MqClient client=new MqClientImpl("sd:tcp://127.0.0.1:9393");

        //订阅
        client.subscribe("demo",((topic,message)->{
            System.out.println("ClientDemo2::"+topic+"-"+message);
        }));

    }
}
