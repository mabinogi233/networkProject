package main.java.client.messageQueue;

import java.util.HashMap;
import java.util.Map;

//多个消息队列管理模块
public class MessageQueueAdm<T1,T2> {

    private Map<T1, MessageQueue<T2>> queues = new HashMap<T1, MessageQueue<T2>>();

    /**
     * 对消息队列id 添加一条信息
     * @param id
     * @param message
     */
    public void write(T1 id,T2 message){
        queues.get(id).push_message(message);

    }

    /**
     * 从消息队列id中读取一条信息
     * @param id
     * @return
     */
    public T2 read(T1 id){
        return queues.get(id).pop_message();
    }

    /**
     * 当id对应的消息队列不存在时，开启一个新的消息队列id
     * @param id
     */
    public void putIfAbsent(T1 id){
        queues.putIfAbsent(id,new MessageQueue<T2>());
    }

    /**
     * 删除id对应的消息队列
     * @param id
     */
    public void delete(T1 id){
        queues.remove(id);
    }

    /**
     * 判断id 对应的消息队列是否存在
     * @param id
     * @return
     */
    public boolean isExist(T1 id){
        return queues.get(id)!=null;
    }

    /**
     * 判断id对应的消息队列中是否存在消息
     * @param id
     * @return
     */
    public boolean isEmtry(T1 id){
        return queues.get(id).empty();
    }
}
