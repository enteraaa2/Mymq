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

public class ClientDemo2 {
    public static void main(String[] args) throws Exception {
        //客户端
        MqClient client=new MqClientImpl("Mymq://127.0.0.1:9393?accessKey=aaa&accessSecretKey=bbb")
                .autoAck(true);;

        //订阅
        client.subscribe("demo",new Subscription("b",((topic, message)->{
            System.out.println("ClientDemo2::"+topic+"-"+message);
        })));

    }
}
