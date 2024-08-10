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
                .autoAck(false);;

        //订阅
        client.subscribe("demo",new Subscription("b",((topic, message)->{
        //测试在前两次发的时候是失败的，等第三次成功
            if(message.getTimes()<2){
                System.out.println("ClientDemo2-no::"+topic+"-"+message);
                message.acknowledge(false);
            }else{
                System.out.println("ClientDemo2-ok::"+topic+"-"+message);
                message.acknowledge(true);
            }
        })));

    }
}
