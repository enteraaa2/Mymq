package demo;

import org.noera.Mymq.client.MqClient;
import org.noera.Mymq.client.MqClientImpl;
import org.noera.Mymq.client.Subscription;

/**
 * @program: Mymq
 * @description: 客户端测试例
 * @author: lydms
 * @create: 2024-08-03 18:36
 **/

public class ClientDemo3 {
    public static void main(String[] args) throws Exception {
        //客户端
        MqClient client=new MqClientImpl("Mymq://127.0.0.1:9393?accessKey=aaa&accessSecretKey=bbb");

        //订阅
        client.subscribe("demo2",new Subscription("c",((topic, message)->{
            System.out.println("ClientDemo3::"+topic+"-"+message);
            message.acknowledge(true);
        })));
    }
}
