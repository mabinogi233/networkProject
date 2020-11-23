package main.java.client.messageQueue;

import java.util.LinkedList;

//基础消息队列，模板方法
public class MessageQueue<T> {
    private LinkedList<T> queue = new LinkedList<T>();
    //串行写
    //做同步，防止多个线程同时写入
    public synchronized void push_message(T message){
        queue.addLast(message);
    }
    //并行读
    public T get_message(){
        return queue.getFirst();
    }
    //弹出消息
    public T pop_message(){
        return queue.removeFirst();
    }

    public boolean empty() {
        return queue.isEmpty();
    }

    public int get_length(){
        return queue.size();
    }
}
