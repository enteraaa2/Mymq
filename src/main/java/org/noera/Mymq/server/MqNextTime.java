package org.noera.Mymq.server;

/**
 * @program: Mymq
 * @description: 当消息消费失败时下次重试时间工具类
 * 作用就是管理消息消费失败后的重试时间策略。它提供了一种机制来决定何时重新尝试派发消息
 * @author: lydms
 * @create: 2024-08-09 23:06
 **/

public class MqNextTime {
    //判断当前时间是否超过了重试时间，如果超过了那就标识可以重新派发
    public static boolean allowDistribute(MqMessageHolder mqMessageHolder){
        if(mqMessageHolder.getNextTime()<=System.currentTimeMillis()){
            return true;
        }else{
            return false;
        }
    }
    //计算下次重试机制
    public static long getNextTime(MqMessageHolder mqMessageHolder) {
        switch (mqMessageHolder.getTimes()) {
            case 0:
                return 0;
            case 1:
                return System.currentTimeMillis()+1000*5;//5s
            case 2:
                return System.currentTimeMillis()+1000*30;//30s
            case 3:
                return System.currentTimeMillis()+1000*60*3;//3m
            case 4:
                return System.currentTimeMillis()+1000*60*9;//9m
            case 5:
                return System.currentTimeMillis()+1000*60*15;//15m
            case 6:
                return System.currentTimeMillis()+1000*60*30;//30m
            case 7:
                return System.currentTimeMillis()+1000*60*60;//60m
            default:
                return System.currentTimeMillis()+1000*60*60*2;//120m
        }
    }
}
