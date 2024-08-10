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

public class ClientDemo1 {
    public static void main(String[] args) throws Exception {
        //客户端
        //鉴权
        MqClient client=new MqClientImpl("Mymq://127.0.0.1:9393?accessKey=aaa&accessSecretKey=bbb")
                .autoAck(true);

        //订阅
        client.subscribe("demo",new Subscription("a",((topic,message)->{
            System.out.println("ClientDemo1::"+topic+"-"+message);
        })));

        //发布
        for (int i = 0; i < 10; i++) {
            Thread.sleep(100);
            client.publish("demo","hi"+i);
            client.publish("demo2","hi"+i);
        }
    }
}
